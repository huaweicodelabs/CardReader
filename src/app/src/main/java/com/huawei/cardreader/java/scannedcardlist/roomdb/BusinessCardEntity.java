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

package com.huawei.cardreader.java.scannedcardlist.roomdb;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * The type Business card entity.
 */
@Entity(indices = {@Index(value = {"aadhar_id"}, unique = true),
        @Index(value = {"mobile_no"}, unique = true),
        @Index(value = {"pan_number"}, unique = true),
        @Index(value = {"account_number"}, unique = true)})
public class BusinessCardEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "companyname")
    private String companyname;


    @ColumnInfo(name = "mobile_no")
    private String mobileno;

    @ColumnInfo(name = "emailid")
    private String emailid;

    @ColumnInfo(name = "jobtitle")
    private String jobtitle;

    @ColumnInfo(name = "website")
    private String website;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "image")
    private String image;


    @ColumnInfo(name = "aadhar_id")
    private String aadharid;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "dob")
    private String dob;

    @ColumnInfo(name = "fathername")
    private String fathername;

    @ColumnInfo(name = "cardType")
    private String cardType;

    @ColumnInfo(name = "pan_number")
    private String pannumber;

    @ColumnInfo(name = "account_number")
    private String accountnumber;

    @ColumnInfo(name = "issuer")
    private String issuer;

    @ColumnInfo(name = "expirydate")
    private String expirydate;

    @ColumnInfo(name = "banktype")
    private String banktype;

    @ColumnInfo(name = "bankorganization")
    private String bankorganization;

    /**
     * Instantiates a new Business card entity.
     */
    /*
     * Getters and Setters
     * */
    public BusinessCardEntity() {
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets companyname.
     *
     * @return the companyname
     */
    public String getCompanyname() {
        return companyname;
    }

    /**
     * Sets companyname.
     *
     * @param companyname the companyname
     */
    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    /**
     * Gets mobileno.
     *
     * @return the mobileno
     */
    public String getMobileno() {
        return mobileno;
    }

    /**
     * Sets mobileno.
     *
     * @param mobileno the mobileno
     */
    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }


    /**
     * Gets emailid.
     *
     * @return the emailid
     */
    public String getEmailid() {
        return emailid;
    }

    /**
     * Sets emailid.
     *
     * @param emailid the emailid
     */
    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    /**
     * Gets website.
     *
     * @return the website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets website.
     *
     * @param website the website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets jobtitle.
     *
     * @return the jobtitle
     */
    public String getJobtitle() {
        return jobtitle;
    }

    /**
     * Sets jobtitle.
     *
     * @param jobtitle the jobtitle
     */
    public void setJobtitle(String jobtitle) {
        this.jobtitle = jobtitle;
    }

    /**
     * Gets image.
     *
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets image.
     *
     * @param image the image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Gets aadharid.
     *
     * @return the aadharid
     */
    public String getAadharid() {
        return aadharid;
    }

    /**
     * Sets aadharid.
     *
     * @param aadharid the aadharid
     */
    public void setAadharid(String aadharid) {
        this.aadharid = aadharid;
    }

    /**
     * Gets gender.
     *
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets gender.
     *
     * @param gender the gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets dob.
     *
     * @return the dob
     */
    public String getDob() {
        return dob;
    }

    /**
     * Sets dob.
     *
     * @param dob the dob
     */
    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * Gets card type.
     *
     * @return the card type
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * Sets card type.
     *
     * @param cardType the card type
     */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /**
     * Gets pannumber.
     *
     * @return the pannumber
     */
    public String getPannumber() {
        return pannumber;
    }

    /**
     * Sets pannumber.
     *
     * @param pannumber the pannumber
     */
    public void setPannumber(String pannumber) {
        this.pannumber = pannumber;
    }

    /**
     * Gets accountnumber.
     *
     * @return the accountnumber
     */
    public String getAccountnumber() {
        return accountnumber;
    }

    /**
     * Sets accountnumber.
     *
     * @param accountnumber the accountnumber
     */
    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    /**
     * Gets issuer.
     *
     * @return the issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets issuer.
     *
     * @param issuer the issuer
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Gets expirydate.
     *
     * @return the expirydate
     */
    public String getExpirydate() {
        return expirydate;
    }

    /**
     * Sets expirydate.
     *
     * @param expirydate the expirydate
     */
    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }

    /**
     * Gets banktype.
     *
     * @return the banktype
     */
    public String getBanktype() {
        return banktype;
    }

    /**
     * Sets banktype.
     *
     * @param banktype the banktype
     */
    public void setBanktype(String banktype) {
        this.banktype = banktype;
    }

    /**
     * Gets bankorganization.
     *
     * @return the bankorganization
     */
    public String getBankorganization() {
        return bankorganization;
    }

    /**
     * Sets bankorganization.
     *
     * @param bankorganization the bankorganization
     */
    public void setBankorganization(String bankorganization) {
        this.bankorganization = bankorganization;
    }

    /**
     * Gets fathername.
     *
     * @return the fathername
     */
    public String getFathername() {
        return fathername;
    }

    /**
     * Sets fathername.
     *
     * @param fathername the fathername
     */
    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    @NonNull
    @Override
    public String toString() {
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
                '}';
    }


}
