/*
 * Copyright © 2013-2016 The Apl Core Developers.
 * Copyright © 2016-2017 Apollo Foundation IP B.V.
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Apollo Foundation B.V.,
 * no part of the Apl software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package apl.http;

import apl.DigitalGoodsStore;
import apl.AplException;
import apl.db.DbIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

public final class GetDGSExpiredPurchases extends APIServlet.APIRequestHandler {

    static final GetDGSExpiredPurchases instance = new GetDGSExpiredPurchases();

    private GetDGSExpiredPurchases() {
        super(new APITag[] {APITag.DGS}, "seller", "firstIndex", "lastIndex");
    }

    @Override
    protected JSONStreamAware processRequest(HttpServletRequest req) throws AplException {

        long sellerId = ParameterParser.getAccountId(req, "seller", true);
        int firstIndex = ParameterParser.getFirstIndex(req);
        int lastIndex = ParameterParser.getLastIndex(req);

        JSONObject response = new JSONObject();
        JSONArray purchasesJSON = new JSONArray();

        try (DbIterator<DigitalGoodsStore.Purchase> purchases = DigitalGoodsStore.Purchase.getExpiredSellerPurchases(sellerId, firstIndex, lastIndex)) {
            while (purchases.hasNext()) {
                purchasesJSON.add(JSONData.purchase(purchases.next()));
            }
        }

        response.put("purchases", purchasesJSON);
        return response;
    }

}