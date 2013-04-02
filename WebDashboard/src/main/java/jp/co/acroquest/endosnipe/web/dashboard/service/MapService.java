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
package jp.co.acroquest.endosnipe.web.dashboard.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.data.dao.MapInfoDao;
import jp.co.acroquest.endosnipe.data.entity.MapInfo;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.manager.DatabaseManager;

import org.springframework.stereotype.Service;

@Service
public class MapService {
	/** ロガー */
	private static final ENdoSnipeLogger LOGGER = ENdoSnipeLogger
			.getLogger(MapService.class);

	/**
	 * 全てのマップデータを返す。
	 * 
	 * @return マップデータ
	 */
	public List<Map<String, String>> getAllMap() {
		DatabaseManager dbMmanager = DatabaseManager.getInstance();
		String dbName = dbMmanager.getDataBaseName(1);
		List<MapInfo> mapList = null;
		try {
			mapList = MapInfoDao.selectAll(dbName);
		} catch (SQLException ex) {
			LOGGER.log(LogMessageCodes.SQL_EXCEPTION);
			return new ArrayList<Map<String, String>>();
		}

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		for (MapInfo mapInfo : mapList) {
			Map<String,String> dataMap = this.converDataMap(mapInfo);
			resultList.add(dataMap);
		}
		return resultList;
	}

	/**
	 * マップを登録する。
	 * 
	 * 
	 */
	public void insert(final MapInfo mapInfo) {
		DatabaseManager dbMmanager = DatabaseManager.getInstance();
		String dbName = dbMmanager.getDataBaseName(1);
		try {
			MapInfoDao.insert(dbName, mapInfo);
		} catch (SQLException ex) {
			LOGGER.log(LogMessageCodes.SQL_EXCEPTION);
		}
	}

	/**
	 * マップを更新する。
	 * 
	 * @param
	 */
	public void update(final MapInfo mapInfo) {
		DatabaseManager dbMmanager = DatabaseManager.getInstance();
		String dbName = dbMmanager.getDataBaseName(1);
		try {
			MapInfoDao.update(dbName, mapInfo);
		} catch (SQLException ex) {
			LOGGER.log(LogMessageCodes.SQL_EXCEPTION);
		}
	}

	/**
	 * マップを取得する。
	 * @param mapId
	 * @return
	 */
	public Map<String, String> getById(long mapId){
		DatabaseManager dbMmanager = DatabaseManager.getInstance();
		String dbName = dbMmanager.getDataBaseName(1);
		try {
			MapInfo mapInfo = MapInfoDao.selectById(dbName, mapId);
			return this.converDataMap(mapInfo);
		} catch (SQLException ex) {
			LOGGER.log(LogMessageCodes.SQL_EXCEPTION);
			return null;
		}
	}

	/**
	 * マップ情報をMap形式に変換する。
	 * @param mapInfo
	 * @return
	 */
	private Map<String, String> converDataMap(MapInfo mapInfo){
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("id", String.valueOf(mapInfo.mapId));
		dataMap.put("parentTreeId", "");
		dataMap.put("data", mapInfo.name);
		dataMap.put("treeId", String.valueOf(mapInfo.mapId));
		dataMap.put("mapData", mapInfo.data);
		return dataMap;
	}
}
