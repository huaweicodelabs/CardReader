/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.cardreader.java.utils.bankcardviewutils;

/**
 * The type Card type.
 */
public class BankCardType {
    /**
     * The constant VISA.
     */
    public static final int BANK_CARD_VISA = 0;

    /**
     * The constant MASTERCARD.
     */
    public static final int BANKCARD_MASTERCARD = 1;

    /**
     * The constant AMERICAN_EXPRESS.
     */
    public static final int BANKCARD_AMERICAN_EXPRESS = 2;

    /**
     * The constant DISCOVER.
     */
    public static final int BANKCARD_DISCOVER = 3;

    /**
     * The constant AUTO.
     */
    public static final int AUTO = 4;
    /**
     * The constant PATTERN_MASTER_CARD.
     */
    protected static final String PATTERN_FOR_MASTER_CARD = "^5[1-5][0-9]{14}$";

    /**
     * The constant PATTERN_AMERICAN_EXPRESS.
     */
    protected static final String PATTERN_FOR_AMERICAN_EXPRESS = "^3[47][0-9]{13}$";

    /**
     * The constant PATTERN_DISCOVER.
     */
    protected static final String PATTERN_FOR_DISCOVER
            = "^65[4-9][0-9]{13}|64[4-9][0-9]{13}|6011[0-9]{12}|" +
            "(622(?:12[6-9]|1[3-9][0-9]|[2-8][0-9][0-9]|9[01][0-9]|92[0-5])[0-9]{10})$";

}
