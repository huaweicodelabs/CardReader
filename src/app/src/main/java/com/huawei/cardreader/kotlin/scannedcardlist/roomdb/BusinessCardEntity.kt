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

package com.huawei.cardreader.kotlin.scannedcardlist.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * The type Business card entity.
 */
@Entity(
    indices = [Index(
        value = ["aadhar_id"],
        unique = true
    ), Index(
        value = ["mobile_no"],
        unique = true
    ), Index(
        value = ["pan_number"],
        unique = true
    ), Index(value = ["account_number"], unique = true)]
)
class BusinessCardEntity
/**
 * Instantiates a new Business card entity.
 */
/*
     * Getters and Setters
     * */
    : Serializable {
    /**
     * Gets id.
     *
     * @return the id
     */
    /**
     * Sets id.
     *
     * @param id the id
     */
    @PrimaryKey(autoGenerate = true)
    var id = 0

    /**
     * Gets name.
     *
     * @return the name
     */
    /**
     * Sets name.
     *
     * @param name the name
     */
    @ColumnInfo(name = "name")
    var name: String? = null

    /**
     * Gets companyname.
     *
     * @return the companyname
     */
    /**
     * Sets companyname.
     *
     * @param companyname the companyname
     */
    @ColumnInfo(name = "companyname")
    var companyname: String? = null

    /**
     * Gets mobileno.
     *
     * @return the mobileno
     */
    /**
     * Sets mobileno.
     *
     * @param mobileno the mobileno
     */
    @ColumnInfo(name = "mobile_no")
    var mobileno: String? = null

    /**
     * Gets emailid.
     *
     * @return the emailid
     */
    /**
     * Sets emailid.
     *
     * @param emailid the emailid
     */
    @ColumnInfo(name = "emailid")
    var emailid: String? = null

    /**
     * Gets jobtitle.
     *
     * @return the jobtitle
     */
    /**
     * Sets jobtitle.
     *
     * @param jobtitle the jobtitle
     */
    @ColumnInfo(name = "jobtitle")
    var jobtitle: String? = null

    /**
     * Gets website.
     *
     * @return the website
     */
    /**
     * Sets website.
     *
     * @param website the website
     */
    @ColumnInfo(name = "website")
    var website: String? = null

    /**
     * Gets address.
     *
     * @return the address
     */
    /**
     * Sets address.
     *
     * @param address the address
     */
    @ColumnInfo(name = "address")
    var address: String? = null

    /**
     * Gets image.
     *
     * @return the image
     */
    /**
     * Sets image.
     *
     * @param image the image
     */
    @ColumnInfo(name = "image")
    var image: String? = null

    /**
     * Gets aadharid.
     *
     * @return the aadharid
     */
    /**
     * Sets aadharid.
     *
     * @param aadharid the aadharid
     */
    @ColumnInfo(name = "aadhar_id")
    var aadharid: String? = null

    /**
     * Gets gender.
     *
     * @return the gender
     */
    /**
     * Sets gender.
     *
     * @param gender the gender
     */
    @ColumnInfo(name = "gender")
    var gender: String? = null

    /**
     * Gets dob.
     *
     * @return the dob
     */
    /**
     * Sets dob.
     *
     * @param dob the dob
     */
    @ColumnInfo(name = "dob")
    var dob: String? = null

    /**
     * Gets fathername.
     *
     * @return the fathername
     */
    /**
     * Sets fathername.
     *
     * @param fathername the fathername
     */
    @ColumnInfo(name = "fathername")
    var fathername: String? = null

    /**
     * Gets card type.
     *
     * @return the card type
     */
    /**
     * Sets card type.
     *
     * @param cardType the card type
     */
    @ColumnInfo(name = "cardType")
    var cardType: String? = null

    /**
     * Gets pannumber.
     *
     * @return the pannumber
     */
    /**
     * Sets pannumber.
     *
     * @param pannumber the pannumber
     */
    @ColumnInfo(name = "pan_number")
    var pannumber: String? = null

    /**
     * Gets accountnumber.
     *
     * @return the accountnumber
     */
    /**
     * Sets accountnumber.
     *
     * @param accountnumber the accountnumber
     */
    @ColumnInfo(name = "account_number")
    var accountnumber: String? = null

    /**
     * Gets issuer.
     *
     * @return the issuer
     */
    /**
     * Sets issuer.
     *
     * @param issuer the issuer
     */
    @ColumnInfo(name = "issuer")
    var issuer: String? = null

    /**
     * Gets expirydate.
     *
     * @return the expirydate
     */
    /**
     * Sets expirydate.
     *
     * @param expirydate the expirydate
     */
    @ColumnInfo(name = "expirydate")
    var expirydate: String? = null

    /**
     * Gets banktype.
     *
     * @return the banktype
     */
    /**
     * Sets banktype.
     *
     * @param banktype the banktype
     */
    @ColumnInfo(name = "banktype")
    var banktype: String? = null

    /**
     * Gets bankorganization.
     *
     * @return the bankorganization
     */
    /**
     * Sets bankorganization.
     *
     * @param bankorganization the bankorganization
     */
    @ColumnInfo(name = "bankorganization")
    var bankorganization: String? = null

    override fun toString(): String {
        return "BusinessCardEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", companyname='" + companyname + '\'' +
                ", mobileno='" + mobileno + '\'' +
                ", emailid='" + emailid + '\'' +
                ", jobtitle='" + jobtitle + '\'' +
                ", website='" + website + '\'' +
                ", address='" + address + '\'' +
                ", cardType='" + cardType + '\'' +
                ", pannumber='" + pannumber + '\'' +
                ", accountnumber='" + accountnumber + '\'' +
                '}'
    }
}