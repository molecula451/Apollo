/******************************************************************************
 * Copyright © 2013-2016 The Apl Core Developers.                             *
 * Copyright © 2016-2017 Apollo Foundation IP B.V.                                     *
 *                                                                            *
 * See the LICENSE.txt file at the top-level directory of this distribution   *
 * for licensing information.                                                 *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement with Apollo Foundation B.V.,*
 * no part of the Apl software, including this file, may be copied, modified, *
 * propagated, or distributed except according to the terms contained in the  *
 * LICENSE.txt file.                                                          *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ******************************************************************************/

QUnit.module("apl.address");

QUnit.test("aplAddress", function (assert) {
    var address = new AplAddress();
    assert.equal(address.set("APL-XK4R-7VJU-6EQG-7R335"), true, "valid address");
    assert.equal(address.toString(), "APL-XK4R-7VJU-6EQG-7R335", "address");
    assert.equal(address.set("APL-XK4R-7VJU-6EQG-7R336"), false, "invalid address");
});