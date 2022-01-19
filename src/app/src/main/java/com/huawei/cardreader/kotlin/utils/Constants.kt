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

package com.huawei.cardreader.kotlin.utils

/**
 * The type Constants.
 */
object Constants {
    /**
     * The constant DEFAULT_VERSION.
     */
    const val DEFAULT_VERSION = "1.0.3.300"

    /**
     * The constant IS_DELETED.
     */
    var IS_DELETED = false

    /**
     * The constant IS_DELETED_FROM_PROFILEDETAILS.
     */
    var IS_DELETED_FROM_PROFILEDETAILS = false

    /**
     * The constant BUSINESS_CARD.
     */
    const val BUSINESS_CARD = "BusinessCard"

    /**
     * The constant AADHARCARD.
     */
    const val AADHARCARD = "AadharCard"

    /**
     * The constant PANCARD.
     */
    const val PANCARD = "PanCard"

    /**
     * The constant BANKCARD.
     */
    const val BANKCARD = "BankCard"

    /**
     * The constant GENERALCARD.
     */
    const val GENERALCARD = "GeneralCard"

    /**
     * The constant GENERALCARD.
     */
    const val GENERALCARDS = "genearlcard"

    /**
     * The constant SAMPLNUMBER.
     */
    const val SAMPLNUMBER = "1234567890"

    /**
     * The constant FEMALE.
     */
    const val FEMALE = "Female"

    /**
     * The constant MALE.
     */
    const val MALE = "Male"

    /**
     * The constant GENDER.
     */
    const val GENDER = "gender"

    /**
     * The constant AADHAR.
     */
    const val AADHAR = "aadhaar"

    /**
     * The constant FATHER.
     */
    const val FATHER = "father"

    /**
     * The constant FATHERNAME.
     */
    const val FATHERNAME = "fatherName"

    /**
     * The constant NAME.
     */
    const val NAME = "name"

    /**
     * The constant YearOfBirth.
     */
    const val YEAROFBIRTH = "yob"

    /**
     * The constant Mobile number REGEX.
     */
    const val REGEX = "^\\+?(\\d[\\d-. ]+)?(\\([\\d-. ]+\\))?[\\d-. ]+\\d$"

    /**
     * The constant PHONENUMBER.
     */
    const val PHONENUMBER = "phonenum"

    /**
     * The constant EMAIL.
     */
    const val EMAIL = "email"

    /**
     * The constant SOLUTIONS.
     */
    const val SOLUTIONS = "solutions"

    /**
     * The constant ORG.
     */
    const val ORG = "org"

    /**
     * The constant TEXT.
     */
    const val TEXT = "text"

    /**
     * The constant emailRegex.
     */
    const val EMAILREGEX = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$"

    /**
     * The constant DateOfBirth.
     */
    const val DATEOFBIRTH = "dob"

    /**
     * The constant PANNUMBER.
     */
    const val PANNUMBER = "pan"

    /**
     * The constant BANKCARDNUMBER.
     */
    const val BANKCARDNUMBER = "Number： "

    /**
     * The constant CARDTYPE.
     */
    const val CARDTYPE = "Type： "

    /**
     * The constant ISSUER.
     */
    const val ISSUER = "Issuer : "

    /**
     * The constant EXPIRE.
     */
    const val EXPIRE = "Expire : "

    /**
     * The constant ORGANIZATION.
     */
    const val ORGANIZATION = "Organization : "

    /**
     * The constant ID.
     */
    const val ID = "id"

    /**
     * The constant QRCODEIMAGE.
     */
    const val QRCODEIMAGE = "qrCodeimage"
    /**
     * The constant QRCODECARDTYPE.
     */
    const val QRCODECARDTYPE = "qrcodecardtype"

    /**
     * The constant ADDRESS.
     */
    const val ADDRESS = "address"

    /**
     * The constant SMSBODY.
     */
    const val SMSBODY = "sms_body"

    /**
     * The constant CARD_TYPE_TO_SAVE.
     */
    const val CARD_TYPE_TO_SAVE = "cardTypetoSave"

    /**
     * The constant SCREENTYPE.
     */
    const val SCREENTYPE = "screenType"

    /**
     * The constant SCANNEDCARDNUMBER.
     */
    const val SCANNEDCARDNUMBER = "scannedCardNumber"

    /**
     * The constant PHONENO_MASK.
     */
    const val PHONENO_MASK = "##xxxxxxx#"

    /**
     * The constant DEFAULT_MASK.
     */
    const val DEFAULT_MASK = "xxxxxxxxx"

    /**
     * The constant NAME_MASK.
     */
    const val NAME_MASK = "xxxxxxx"

    /**
     * The constant EXPIRY_MASK.
     */
    const val EXPIRY_MASK = "xxxxx"

    /**
     * The constant CARD_MASK.
     */
    const val CARD_MASK = "xxxxxxx"
}