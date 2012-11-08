/*
 * Copyright (c) 2012 Acroquest Technology Co., Ltd. All Rights Reserved.
 * Please read the associated COPYRIGHTS file for more details.
 *
 * THE SOFTWARE IS PROVIDED BY Acroquest Technology Co., Ltd., WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDER BE LIABLE FOR ANY
 * CLAIM, DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
#include "jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader.h"

#include <fcntl.h>
#include <unistd.h> 
#include <kstat.h>
#include <strings.h>
#include <string.h>
#include <stdlib.h>
#include <sys/sysinfo.h>
#include <sys/swap.h>
#include <errno.h>

/* 事前に以下の設定をしないと旧式のprocfs定義が呼びこまれるため注意が必要 */
#define _STRUCTURED_PROC 1
#define prpsinfo psinfo
#include <sys/procfs.h>

#define PROC_INFO "/proc/self/psinfo"
#define PROC_USAGE "/proc/self/usage"
#define CPUSTAT "cpu_stat"
#define VMINFO "vminfo"

/*================================================================================*/
/** 構造体定義 **/

/* システム全体の計測値を保存する構造体 */
struct system_info
{
  jlong cpu_sys;   /* CPU(システム) 単位:nano second */
  jlong cpu_user;  /* CPU(ユーザ)   単位:nano second */
  jlong cpu_total; /* CPU(全体)     単位:nano second */

  jlong mem_free;  /* 実メモリ(空き) 単位:byte  */
  jlong mem_total; /* 実メモリ(全体) 単位:byte  */

  jlong swap_free;  /* スワップ(空き) 単位:byte */
  jlong swap_avail; /* スワップ(使用) 単位:byte */
  jlong updates;    /* スワップ情報更新回数 */

  jlong page_in;    /* ページin回数 */
  jlong page_out;   /* ページout回数 */
};

/* プロセスの計測値を保存する構造体 */
struct process_info
{
  jlong cpu_sys;   /* CPU(システム) 単位:nano second */
  jlong cpu_user;  /* CPU(ユーザ)   単位:nano second */
  jlong cpu_total; /* CPU(全体)     単位:nano second */

  jlong mem_virtual;  /* 仮想メモリ使用量 単位:byte  */
  jlong mem_physical; /* 実メモリ使用量 単位:byte  */

  jint threads; /* スレッド数 */

  jint majar_fault; /* メジャーフォルト回数 */
};

/*================================================================================*/
/** 前宣言 **/

static jboolean getprocessinfo(struct process_info* info);
static jboolean getsysinfo(struct system_info* info);

/*================================================================================*/
/** マクロ **/

/* kbyte -> byte */
#define KB2BYTE(kb) ((kb) * 1024)

/* ticks -> nano second */
#define TICK2NSEC(tick) ((tick) * 10 * (jlong)1000 * (jlong)1000)

/* times_truct_t -> nano second */
#define TIMESTRUCT2NSEC(ts) (((jlong)(ts).tv_sec * (jlong)1000 * (jlong)1000 * (jlong)1000 + (ts).tv_nsec))

/*================================================================================*/
/** グローバル変数 **/

/* kstatのハンドラ */
kstat_ctl_t* g_kstat_ctl = NULL;

/* cpu数 */
int g_num_cpus = 1;

/* システム全体の計測値　*/
struct system_info g_system_info;

/* システム全体の計測値　*/
struct system_info g_system_info_prev;

/* プロセスの計測値　*/
struct process_info g_process_info;

/* kstatの更新がなかった(=updatesが増加しない)場合用の前の計測値　*/
jlong g_swap_free_prev = 0;
jlong g_swap_total_prev = 0;

/*================================================================================*/
/** JNI関数の定義 **/

JNIEXPORT jboolean JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_openQuery
  (JNIEnv *env, jobject obj)
{
  kstat_t *kstat_ncpus;
  kstat_named_t *knamed_ncpus;
  int result;

  g_kstat_ctl = kstat_open();

  /* get number of cpus */
  kstat_ncpus = kstat_lookup(g_kstat_ctl, "unix", 0, "system_misc");
  result = kstat_read(g_kstat_ctl, kstat_ncpus, 0);
  if (result == -1)
  {
    return JNI_FALSE;
  }
  
  knamed_ncpus = kstat_data_lookup(kstat_ncpus, "ncpus");
  if (knamed_ncpus != NULL)
  {
    g_num_cpus = knamed_ncpus->value.ui32;
  }
  if (g_num_cpus <= 0)
  {
    g_num_cpus = 1;
  }

  getsysinfo(&g_system_info_prev);
  
  return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_collectQueryData
  (JNIEnv *env, jobject obj)
{
  jboolean result_system;
  jboolean result_process;
  
  g_system_info_prev = g_system_info;
  result_system = getsysinfo(&g_system_info);
  result_process = getprocessinfo(&g_process_info);

  return (result_system == JNI_TRUE) && (result_process == JNI_TRUE);
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getSystemCPUSys
  (JNIEnv *env, jobject obj)
{
  return g_system_info.cpu_sys;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getSystemCPUUser
  (JNIEnv *env, jobject obj)
{
  return g_system_info.cpu_user;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getSystemCPUTotal
  (JNIEnv *env, jobject obj)
{
  return g_process_info.cpu_total;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getSystemMemoryFree
  (JNIEnv *env, jobject obj)
{
  return g_system_info.mem_free;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getSystemMemoryTotal
  (JNIEnv *env, jobject obj)
{
  return g_system_info.mem_total;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getSystemSwapFree
  (JNIEnv *env, jobject obj)
{
  if (g_system_info.updates != g_system_info_prev.updates)
  {
    g_swap_free_prev = (g_system_info.swap_free - g_system_info_prev.swap_free) 
                     / (g_system_info.updates - g_system_info_prev.updates);
  }
  return g_swap_free_prev;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getSystemSwapTotal
  (JNIEnv *env, jobject obj)
{
  if (g_system_info.updates != g_system_info_prev.updates)
  {
    g_swap_total_prev = ((g_system_info.swap_avail - g_system_info_prev.swap_avail)
                       + (g_system_info.swap_free - g_system_info_prev.swap_free))
                      / (g_system_info.updates - g_system_info_prev.updates);
  }
  return g_swap_total_prev;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getSystemPageIn
  (JNIEnv *env, jobject obj)
{
  return g_system_info.page_in;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getSystemPageOut
  (JNIEnv *env, jobject obj)
{
  return g_system_info.page_out;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getProcessCPUUser
  (JNIEnv *env, jobject obj)
{
  return g_process_info.cpu_user;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getProcessCPUSys
  (JNIEnv *env, jobject obj)
{
  return g_process_info.cpu_sys;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getProcessMajFlt
  (JNIEnv *env, jobject obj)
{
  return g_process_info.majar_fault;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getProcessMemoryVirtual
  (JNIEnv *env, jobject obj)
{
  return g_process_info.mem_virtual;
}

JNIEXPORT jlong JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getProcessMemoryPhysical
  (JNIEnv *env, jobject obj)
{
  return g_process_info.mem_physical;
}

JNIEXPORT jint JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_getNumThreads
  (JNIEnv *env, jobject obj)
{
  return g_process_info.threads;
}

JNIEXPORT jboolean JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_closeQuery
  (JNIEnv *env, jobject obj)
{
  kstat_close(g_kstat_ctl);
  g_kstat_ctl = NULL;
}

/*================================================================================*/
/** ヘルパー関数の定義 **/

/** スワップ使用量を取得する */
int get_swapinfo(uint64_t *pavail, uint64_t *pfree, uint64_t *updates) 
{
  kstat_t *kstat_vminfo;
  kstat_t *kstat_sysinfo;
  kstat_named_t *kname_freemem;
  kstat_named_t *kname_availmem;
  kstat_named_t *kname_updates;
  vminfo_t vminfo;
  sysinfo_t sysinfo;
  int result_vminfo;
  int result_sysinfo;

  if (pavail == NULL || pfree == NULL || updates == NULL)
  {
    return JNI_FALSE;
  }

  kstat_vminfo = kstat_lookup(g_kstat_ctl, "unix", 0, "vminfo");
  if (kstat_vminfo == NULL)
  {
    return JNI_FALSE;
  }

  result_vminfo = kstat_read(g_kstat_ctl, kstat_vminfo, &vminfo);
  if (result_vminfo == -1)
  {
    return JNI_FALSE;
  }

  *pavail = vminfo.swap_avail;
  *pfree = vminfo.swap_resv;
  
  kstat_sysinfo = kstat_lookup(g_kstat_ctl, "unix", 0, "sysinfo");
  if (kstat_sysinfo == NULL)
  {
    return JNI_FALSE;
  }

  result_sysinfo = kstat_read(g_kstat_ctl, kstat_sysinfo, &sysinfo);
  if (result_sysinfo == -1)
  {
    return JNI_FALSE;
  }
  *updates = sysinfo.updates;

  return (0);
}

/** システム全体の情報を取得する  */
static jboolean getsysinfo(struct system_info* info)
{
  int pagesize = sysconf(_SC_PAGESIZE);

  if (info == NULL)
  {
    return JNI_FALSE;
  }

  /* get memory usage */
  {
    kstat_t *kstat_syspage;
    kstat_named_t *kname_freemem;
    kstat_named_t *kname_physmem;
    int result_syspage;

    kstat_syspage = kstat_lookup(g_kstat_ctl, "unix", 0, "system_pages");
    if (kstat_syspage == NULL)
    {
      return JNI_FALSE;
    }
    
    result_syspage = kstat_read(g_kstat_ctl, kstat_syspage, 0);
    if (result_syspage == -1)
    {
      return JNI_FALSE;
    }

    kname_freemem = kstat_data_lookup(kstat_syspage, "freemem");
    if (kname_freemem != NULL)
    {
      info->mem_free = kname_freemem->value.ul;
    }

    kname_physmem = kstat_data_lookup(kstat_syspage, "physmem");
    if (kname_physmem != NULL)
    {
      info->mem_total = kname_physmem->value.ul;
    }

    info->mem_free *= pagesize;
    info->mem_total *= pagesize;
  }

  /* get swap usage */
  {
    uint64_t swap_avail;
    uint64_t swap_free;
    uint64_t updates;
    get_swapinfo(&swap_avail, &swap_free, &updates);
    
    info->swap_avail = swap_avail * pagesize;
    info->swap_free  = swap_free * pagesize;
    info->updates = updates;

    }
  
  /* cpu usage */
  /* page-in page-out */
  {
    jlong cpu_user  = 0;
    jlong cpu_sys   = 0;
    jlong cpu_total = 0;
    jlong page_out = 0;
    jlong page_in  = 0;
    
    kstat_t *kstat_cpu;
    for (kstat_cpu = g_kstat_ctl->kc_chain; kstat_cpu != NULL; kstat_cpu = kstat_cpu->ks_next)
    {
      int isCPUSTAT = strncmp(kstat_cpu->ks_name, CPUSTAT, sizeof(CPUSTAT) / sizeof(CPUSTAT[0]) - 1);
      if (isCPUSTAT == 0)
      {
        cpu_stat_t cpu_stat;
        kstat_read(g_kstat_ctl, kstat_cpu, &cpu_stat);

        cpu_user  += cpu_stat.cpu_sysinfo.cpu[CPU_USER];

        cpu_sys   += cpu_stat.cpu_sysinfo.cpu[CPU_KERNEL];

        cpu_total += cpu_stat.cpu_sysinfo.cpu[CPU_IDLE];
        cpu_total += cpu_stat.cpu_sysinfo.cpu[CPU_KERNEL];
        cpu_total += cpu_stat.cpu_sysinfo.cpu[CPU_USER];
        cpu_total += cpu_stat.cpu_sysinfo.cpu[CPU_WAIT];
        cpu_total += cpu_stat.cpu_sysinfo.wait[W_IO];
        cpu_total += cpu_stat.cpu_sysinfo.wait[W_SWAP];
        cpu_total += cpu_stat.cpu_sysinfo.wait[W_PIO];
        
        page_out += cpu_stat.cpu_vminfo.pgpgout;
        page_in  += cpu_stat.cpu_vminfo.pgpgin;
      }
    }

    info->cpu_sys   = TICK2NSEC(cpu_sys) / g_num_cpus;
    info->cpu_user  = TICK2NSEC(cpu_user) / g_num_cpus;
    info->cpu_total = TICK2NSEC(cpu_total) / g_num_cpus;
    info->page_in  = page_in * pagesize;
    info->page_out = page_out * pagesize;
  }
}

/** プロセス単体の情報を取得する  */
static jboolean getprocessinfo(struct process_info* info)
{
  if (info == NULL)
  {
    return JNI_FALSE;
  }

  /* get process info */
  {
    struct psinfo procinfo;
    int fd_proc;
    int size_proc;

    fd_proc = open(PROC_INFO, O_RDONLY);
    if (fd_proc < 0)
    {
      return JNI_FALSE;
    }

    size_proc = read(fd_proc, &procinfo, sizeof(struct psinfo));
    if (size_proc != sizeof(struct psinfo))
    {
      close(fd_proc);
      return JNI_FALSE;
    }
    close(fd_proc);

    info->mem_virtual = KB2BYTE(procinfo.pr_size);
    info->mem_physical = KB2BYTE(procinfo.pr_rssize);
    info->threads = procinfo.pr_nlwp;
  }

  /* get process cpu usage */
  {
    struct prusage usageinfo;
    int fd_usage;
    int size_usage;

    fd_usage = open (PROC_USAGE, O_RDONLY);
    if (fd_usage < 0)
    {
      return JNI_FALSE;
    }
    size_usage = read(fd_usage, &usageinfo, sizeof(struct prusage));
    if (size_usage != sizeof(struct prusage)) 
    {
      close(fd_usage);
      return JNI_FALSE;
    }
    close(fd_usage);

    info->cpu_sys     = TIMESTRUCT2NSEC(usageinfo.pr_stime) / (jlong)g_num_cpus;
    info->cpu_user    = TIMESTRUCT2NSEC(usageinfo.pr_utime) / (jlong)g_num_cpus;
    info->cpu_total   = TIMESTRUCT2NSEC(usageinfo.pr_rtime) / (jlong)g_num_cpus;
    info->majar_fault = usageinfo.pr_majf;
  }

  return JNI_TRUE;
}

#ifdef STANDALONE

/*================================================================================*/
/** デバッグ関数の定義 **/

#include <stdlib.h>
#include <stdio.h>

int main(int argc, char *argv[]) 
{
  int index = 0;
  struct system_info  info_prev;
  struct system_info  info;

  {
    jobject obj;
    Java_jp_co_acroquest_endosnipe_javelin_resource_proc_SolarisResourceReader_openQuery(NULL, obj);
  }

  getsysinfo(&info_prev);
  sleep(2);
  for (index = 0; index < 100; index++)
  {
	long update_delta;
	long swap_avail;
	long swap_free;
    struct process_info info2;

    getsysinfo(&info);
    update_delta = info.updates - info_prev.updates;
    swap_avail = info.swap_avail - info_prev.swap_avail;
    swap_free = info.swap_free - info_prev.swap_free;

    printf("sys  cpu(%lld/%lld/%lld), memory(%lld/%lld), swap(%ld/%ld), page(%lld/%lld)\n",
           info.cpu_user, info.cpu_sys, info.cpu_total,
           info.mem_total, 
           info.mem_total - info.mem_free,
           swap_avail / update_delta / 1024, 
           (swap_avail + swap_free) / update_delta / 1024, 
           info.page_in, info.page_out);

    getprocessinfo(&info2);
    printf("proc cpu(%lld/%lld/%lld), memory(%lld/%lld), thread(%d), majflt(%d)\n",
           info2.cpu_user, info2.cpu_sys, info2.cpu_total,
           info2.mem_virtual, info2.mem_physical,
           info2.threads,
           info2.majar_fault);

    info_prev = info;
    sleep(1);
  }  
}

#endif
