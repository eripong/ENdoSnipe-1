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
package jp.co.acroquest.endosnipe.web.dashboard.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.data.entity.MapInfo;
import jp.co.acroquest.endosnipe.web.dashboard.manager.EventManager;
import jp.co.acroquest.endosnipe.web.dashboard.manager.ResourceSender;
import jp.co.acroquest.endosnipe.web.dashboard.service.MapService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wgp.manager.WgpDataManager;

@Controller
@RequestMapping("/map")
public class MapController {

	@Autowired
	protected WgpDataManager wgpDataManager;

	@Autowired
	protected ResourceSender resourceSender;
	/** マップを表すID */
	private static final String MAP_DATA_ID = "map";

	@Autowired
	protected MapService mapService;

	/**
	 * Get Map List.
	 * @return
	 */
	@RequestMapping(value = "/mapList", method = RequestMethod.GET)
	public String initializeMapList() {
		// TODO ServletContextから取得できないため、初期化時に設定する。
		EventManager eventManager = EventManager.getInstance();
		eventManager.setWgpDataManager(wgpDataManager);
		eventManager.setResourceSender(resourceSender);
		return "MapList";
	}

	/**
	 * Get All map Data for Tree.
	 */
	@RequestMapping(value = "/getAll", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<Map<String, String>>> getAll() {
		Map<String, List<Map<String, String>>> resultMap = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> resultList = this.mapService.getAllMap();
		resultMap.put(MAP_DATA_ID, resultList);
		return resultMap;
	}

	/**
	 * Insert Map.
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public void insert(
			@RequestParam(value = "data") final String data,
			@RequestParam(value = "name") final String name) {
		MapInfo mapInfo = new MapInfo();
		mapInfo.data = data;
		mapInfo.name = name;
		this.mapService.insert(mapInfo);
	}

	/**
	 * Update Map.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public void update(
			@RequestParam(value = "mapId") final String mapId,
			@RequestParam(value = "data") final String data,
			@RequestParam(value = "name") final String name) {
		MapInfo mapInfo = new MapInfo();
		mapInfo.mapId = Long.valueOf(mapId);
		mapInfo.data = data;
		mapInfo.name = name;
		this.mapService.update(mapInfo);
	}

	/**
	 * Get Map.
	 */
	@RequestMapping(value = "/getById", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> getById(
			@RequestParam(value = "mapId") final String mapId){
		Map<String, String> responseMap = this.mapService.getById(Long.valueOf(mapId));
		return responseMap;
	}
}
