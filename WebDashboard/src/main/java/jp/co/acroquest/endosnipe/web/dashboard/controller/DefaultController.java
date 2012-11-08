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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import jp.co.acroquest.endosnipe.web.dashboard.manager.EventManager;
import jp.co.acroquest.endosnipe.web.dashboard.manager.ResourceSender;
import jp.co.acroquest.endosnipe.web.dashboard.service.TreeMenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.wgp.manager.WgpDataManager;

@Controller
public class DefaultController
{

    @Autowired
    protected TreeMenuService treeMenuService;

    @Autowired
    protected WgpDataManager wgpDataManager;

    @Autowired
    protected ResourceSender resourceSender;

    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public String initialize(final Locale locale, final Model model,
            final HttpServletRequest request)
    {
        // TODO ServletContextから取得できないため、初期化時に設定する。
        EventManager eventManager = EventManager.getInstance();
        eventManager.setWgpDataManager(wgpDataManager);
        eventManager.setResourceSender(resourceSender);
        return "WebDashboard";
    }
}
