/*******************************************************************************
 * ENdoSnipe 5.0 - (https://github.com/endosnipe)
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package jp.co.acroquest.endosnipe.data.service;

import java.sql.SQLException;
import java.util.List;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.data.dao.AbstractDaoTest;
import jp.co.acroquest.endosnipe.data.dao.HostInfoDao;
import jp.co.acroquest.endosnipe.data.entity.HostInfo;

/**
 * ÉzÉXÉgèÓïÒä«óùã@î\Ç…ëŒÇ∑ÇÈÉeÉXÉgÉNÉâÉX
 * 
 * @author M.Yoshida
 *
 */
public class HostInfoManagerTest extends AbstractDaoTest
{

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }
    
    
    public void testGetHostInfo1()
    {
        // èåè
        // <-- DBÇ…ÉfÅ[É^Ç™ñ≥Ç¢ -->
        
        // é¿é{
        HostInfo result = HostInfoManager.getHostInfo(DB_NAME, true);
        
        // åãâ ämîF
        assertNull(result);
        
        assertCalled(ENdoSnipeLogger.class, "log");
        int logCalled = getCallCount(ENdoSnipeLogger.class, "log");
        
        // ÉGÉâÅ[ÉçÉOÇ™èoóÕÇ≥ÇÍÇ»Ç¢Ç±Ç∆ÇämîFÇ∑ÇÈÅB
        assertFalse(getArgument(ENdoSnipeLogger.class, "log", logCalled - 1, 1) instanceof SQLException);
    }

    public void testGetHostInfo2()
    {
        // èåè
        // <-- DBÇ…ÉfÅ[É^Ç™ñ≥Ç¢ -->
        
        // é¿é{
        HostInfo result = HostInfoManager.getHostInfo(DB_NAME, false);
        
        // åãâ ämîF
        assertNull(result);

        assertCalled(ENdoSnipeLogger.class, "log");
        int logCalled = getCallCount(ENdoSnipeLogger.class, "log");
        
        assertFalse("EEDA0103".equals(getArgument(ENdoSnipeLogger.class, "log", logCalled - 1, 0)));
    }

    public void testGetHostInfo3()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{"mica, 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfo result = HostInfoManager.getHostInfo(DB_NAME, true);
        
        // åãâ ämîF
        assertNotNull(result);
        
        assertEquals("mica", result.hostName);
        assertEquals("192.168.252.22", result.ipAddress);
        assertEquals(15002, result.port);
        assertEquals("ï‚ë´ÇPÇQ", result.description);

        assertCalled(ENdoSnipeLogger.class, "log");
        int logCalled = getCallCount(ENdoSnipeLogger.class, "log");
        
        assertFalse("EEDA0103".equals(getArgument(ENdoSnipeLogger.class, "log", logCalled - 1, 0)));
    }

    public void testGetHostInfo4()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{"mica, 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfo result = HostInfoManager.getHostInfo(DB_NAME, false);
        
        // åãâ ämîF
        assertNotNull(result);
        
        assertEquals("mica", result.hostName);
        assertEquals("192.168.252.22", result.ipAddress);
        assertEquals(15002, result.port);
        assertEquals("ï‚ë´ÇPÇQ", result.description);

        assertCalled(ENdoSnipeLogger.class, "log");
        int logCalled = getCallCount(ENdoSnipeLogger.class, "log");
        
        assertFalse("EEDA0103".equals(getArgument(ENdoSnipeLogger.class, "log", logCalled - 1, 0)));
    }
    
    public void testRegisterHostInfo1()
    {
        // èåè
        // <-- ÉzÉXÉgèÓïÒñ≥Çµ -->
        
        // é¿é{
        HostInfoManager.registerHostInfo(DB_NAME, "mica", "192.168.252.22", 15002, "ï‚ë´ÇPÇQ");
        
        // åãâ ämîF
        HostInfo expected = new HostInfo();
        expected.hostName = "mica";
        expected.ipAddress = "192.168.252.22";
        expected.port = 15002;
        expected.description = "ï‚ë´ÇPÇQ";
        
        assertRegistedDataEqual(expected);
    }

    public void testRegisterHostInfo2()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{"mica, 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfoManager.registerHostInfo(DB_NAME, "mica", "192.168.252.22", 15002, "ï‚ë´ÇPÇQ");
        
        // åãâ ämîF
        HostInfo expected = new HostInfo();
        expected.hostName = "mica";
        expected.ipAddress = "192.168.252.22";
        expected.port = 15002;
        expected.description = "ï‚ë´ÇPÇQ";
        
        assertRegistedDataEqual(expected);
        
        assertNotCalled(HostInfoDao.class, "deleteAll");
    }
    
    public void testRegisterHostInfo3()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{"mica, 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfoManager.registerHostInfo(DB_NAME, "garnet", "192.168.252.22", 15002, "ï‚ë´ÇPÇQ");
        
        // åãâ ämîF
        HostInfo expected = new HostInfo();
        expected.hostName = "garnet";
        expected.ipAddress = "192.168.252.22";
        expected.port = 15002;
        expected.description = "ï‚ë´ÇPÇQ";
        
        assertRegistedDataEqual(expected);
        
        assertCalled(HostInfoDao.class, "deleteAll");
    }

    public void testRegisterHostInfo4()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{"mica, 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfoManager.registerHostInfo(DB_NAME, "mica", "212.168.252.22", 15002, "ï‚ë´ÇPÇQ");
        
        // åãâ ämîF
        HostInfo expected = new HostInfo();
        expected.hostName = "mica";
        expected.ipAddress = "212.168.252.22";
        expected.port = 15002;
        expected.description = "ï‚ë´ÇPÇQ";
        
        assertRegistedDataEqual(expected);
        
        assertCalled(HostInfoDao.class, "deleteAll");
    }

    public void testRegisterHostInfo5()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{"mica, 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfoManager.registerHostInfo(DB_NAME, "mica", "192.168.252.22", 15432, "ï‚ë´ÇPÇQ");
        
        // åãâ ämîF
        HostInfo expected = new HostInfo();
        expected.hostName = "mica";
        expected.ipAddress = "192.168.252.22";
        expected.port = 15432;
        expected.description = "ï‚ë´ÇPÇQ";
        
        assertRegistedDataEqual(expected);
        
        assertCalled(HostInfoDao.class, "deleteAll");
    }
    
    public void testRegisterHostInfo6()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{"mica, 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfoManager.registerHostInfo(DB_NAME, "mica", "192.168.252.22", 15002, "ê‡ñæÇRÇX");
        
        // åãâ ämîF
        HostInfo expected = new HostInfo();
        expected.hostName = "mica";
        expected.ipAddress = "192.168.252.22";
        expected.port = 15002;
        expected.description = "ê‡ñæÇRÇX";
        
        assertRegistedDataEqual(expected);
        
        assertCalled(HostInfoDao.class, "deleteAll");
    }

    public void testRegisterHostInfo7()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{"mica, 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfoManager.registerHostInfo(DB_NAME, "", "192.168.252.22", 15002, "ê‡ñæÇPÇQ");
        
        // åãâ ämîF
        HostInfo expected = new HostInfo();
        expected.hostName = "";
        expected.ipAddress = "192.168.252.22";
        expected.port = 15002;
        expected.description = "ê‡ñæÇPÇQ";
        
        assertRegistedDataEqual(expected);
        
        assertCalled(HostInfoDao.class, "deleteAll");
    }

    public void testRegisterHostInfo8()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{"mica, 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfoManager.registerHostInfo(DB_NAME, "mica", "", 15002, "ê‡ñæÇPÇQ");
        
        // åãâ ämîF
        HostInfo expected = new HostInfo();
        expected.hostName = "mica";
        expected.ipAddress = "";
        expected.port = 15002;
        expected.description = "ê‡ñæÇPÇQ";
        
        assertRegistedDataEqual(expected);
        
        assertCalled(HostInfoDao.class, "deleteAll");
    }

    public void testRegisterHostInfo9()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{", 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfoManager.registerHostInfo(DB_NAME, "mica", "192.168.252.22", 15002, "ï‚ë´ÇPÇQ");
        
        // åãâ ämîF
        HostInfo expected = new HostInfo();
        expected.hostName = "mica";
        expected.ipAddress = "192.168.252.22";
        expected.port = 15002;
        expected.description = "ï‚ë´ÇPÇQ";
        
        assertRegistedDataEqual(expected);
        
        assertCalled(HostInfoDao.class, "deleteAll");
    }
    
    public void testRegisterHostInfo10()
    {
        // èåè
        try
        {
            initializeHostInfoTable(new String[]{"mica, 192.168.252.22, 15002, ï‚ë´ÇPÇQ"});
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // é¿é{
        HostInfoManager.registerHostInfo(DB_NAME, "", "192.168.252.22", 15002, "ï‚ë´ÇPÇQ");
        
        // åãâ ämîF
        HostInfo expected = new HostInfo();
        expected.hostName = "";
        expected.ipAddress = "192.168.252.22";
        expected.port = 15002;
        expected.description = "ï‚ë´ÇPÇQ";
        
        assertRegistedDataEqual(expected);
        
        assertCalled(HostInfoDao.class, "deleteAll");
    }
    
    private void assertRegistedDataEqual(HostInfo expect)
    {
        List<HostInfo> actual = null;
        try
        {
            actual = HostInfoDao.selectAll(DB_NAME);
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(1, actual.size());
        
        HostInfo actualData = actual.get(0);
        
        assertEquals(expect.hostName, actualData.hostName);
        assertEquals(expect.ipAddress, actualData.ipAddress);
        assertEquals(expect.port, actualData.port);
        assertEquals(expect.description, actualData.description);
    }
    
    private void initializeHostInfoTable(String[] datarows) throws SQLException
    {
        if(datarows == null)
        {
            return;
        }
        
        for(String row : datarows)
        {
            String[] elements = row.split(",");
            
            HostInfo info = new HostInfo();
            info.hostName = elements[0].trim();
            info.ipAddress = elements[1].trim();
            info.port = Integer.parseInt(elements[2].trim());
            info.description = elements[3].trim();
            
            HostInfoDao.insert(DB_NAME, info);
        }
    }
    
    
}
