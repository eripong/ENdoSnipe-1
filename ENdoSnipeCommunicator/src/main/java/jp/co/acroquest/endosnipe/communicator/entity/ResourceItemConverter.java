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
package jp.co.acroquest.endosnipe.communicator.entity;

import java.util.List;

import jp.co.acroquest.endosnipe.common.entity.ResourceItem;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

/**
 * JMX計測値を文字列として通信するためのコンバータクラスです。
 * 
 * @author y_asazuma
 */
public class ResourceItemConverter
{
    /**
     * ResourceItemのリストをJSONでエンコード／デコードするための内部クラス
     * 
     * @author y_asazuma
     */
    private class ListOfResourceItem
    {
        private List<ResourceItem> list_;

        public List<ResourceItem> getList()
        {
            return list_;
        }

        public void setList(List<ResourceItem> list)
        {
            list_ = list;
        }
    }

    private static ResourceItemConverter instance_ = new ResourceItemConverter();

    public static ResourceItemConverter getInstance()
    {
        return instance_;
    }

    /**
     * 計測値のリストをJSON形式にエンコードします。
     * 
     * @param itemList 計測値リスト
     * @return JSON形式の計測値リスト
     * 
     * @throws Exception 変換エラー時の例外
     */
    public String encodeToJSON(List<ResourceItem> itemList)
        throws Exception
    {
        if (itemList.size() <= 0)
            return null;

        String jsonStr;
        try
        {
            ListOfResourceItem item = new ListOfResourceItem();
            item.setList(itemList);
            jsonStr = JSON.encode(item);
        }
        catch (JSONException je)
        {
            throw (Exception)je;
        }

        return jsonStr;
    }

    /**
     * JSON形式の計測値のリストをデコードします。
     * @param jsonStr JSON形式の計測値リスト
     * @return 計測値リスト
     * 
     * @throws Exception 変換エラー時の例外
     */
    public List<ResourceItem> decodeFromJSON(String jsonStr)
        throws Exception
    {
        if (jsonStr == null || jsonStr.equals(""))
            return null;

        ListOfResourceItem item;
        try
        {
            item = JSON.decode(jsonStr, ListOfResourceItem.class);
        }
        catch (JSONException je)
        {
            throw (Exception)je;
        }

        return item.getList();
    }
}
