/*
 * Copyright © 2018 Apollo Foundation
 */

package com.apollocurrency.aplwallet.apl.http;

import com.apollocurrency.aplwallet.apl.AplException;
import com.apollocurrency.aplwallet.apl.Constants;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

public class GetAccounts extends APIServlet.APIRequestHandler {
    private static class GetAccountsHolder {
        private static final GetAccounts INSTANCE = new GetAccounts();
    }

    public static GetAccounts getInstance() {
        return GetAccountsHolder.INSTANCE;
    }
    private GetAccounts() {
        super(new APITag[] {APITag.INFO}, "numberOfAccounts");
    }

    @Override
    protected JSONStreamAware processRequest(HttpServletRequest request) throws AplException {
        int numberOfAccounts = ParameterParser.getInt(request, "numberOfAccounts", Constants.MIN_TOP_ACCOUNTS_NUMBER,
                Constants.MAX_TOP_ACCOUNTS_NUMBER, false);
        return JSONData.getAccountsStatistic(Math.max(numberOfAccounts, Constants.MIN_TOP_ACCOUNTS_NUMBER));
    }
}
