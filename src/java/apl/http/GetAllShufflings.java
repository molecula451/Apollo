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

import apl.Shuffling;
import apl.db.DbIterator;
import apl.db.DbUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

public final class GetAllShufflings extends APIServlet.APIRequestHandler {

    static final GetAllShufflings instance = new GetAllShufflings();

    private GetAllShufflings() {
        super(new APITag[] {APITag.SHUFFLING}, "includeFinished", "includeHoldingInfo", "finishedOnly", "firstIndex", "lastIndex");
    }

    @Override
    protected JSONStreamAware processRequest(HttpServletRequest req) {

        boolean includeFinished = "true".equalsIgnoreCase(req.getParameter("includeFinished"));
        boolean finishedOnly = "true".equalsIgnoreCase(req.getParameter("finishedOnly"));
        boolean includeHoldingInfo = "true".equalsIgnoreCase(req.getParameter("includeHoldingInfo"));
        int firstIndex = ParameterParser.getFirstIndex(req);
        int lastIndex = ParameterParser.getLastIndex(req);

        JSONObject response = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        response.put("shufflings", jsonArray);
        DbIterator<Shuffling> shufflings = null;
        try {
            if (finishedOnly) {
                shufflings = Shuffling.getFinishedShufflings(firstIndex, lastIndex);
            } else if (includeFinished) {
                shufflings = Shuffling.getAll(firstIndex, lastIndex);
            } else {
                shufflings = Shuffling.getActiveShufflings(firstIndex, lastIndex);
            }
            for (Shuffling shuffling : shufflings) {
                jsonArray.add(JSONData.shuffling(shuffling, includeHoldingInfo));
            }
        } finally {
            DbUtils.close(shufflings);
        }
        return response;
    }

}