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

package com.huawei.cardreader.java.scannedcardlist.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.cardreader.R;
import com.huawei.cardreader.databinding.ContactListItemBinding;
import com.huawei.cardreader.java.scannedcardlist.activity.ScannedCardListActivity;
import com.huawei.cardreader.java.scannedcardlist.model.QRcodmodel;
import com.huawei.cardreader.java.userauthentication.activity.AuthenticationPage;
import com.huawei.cardreader.java.utils.Constants;
import com.huawei.cardreader.java.utils.DataConverter;

import java.util.List;

import static com.huawei.cardreader.java.utils.Constants.AADHARCARD;
import static com.huawei.cardreader.java.utils.Constants.BUSINESS_CARD;
import static com.huawei.cardreader.java.utils.Constants.ID;
import static com.huawei.cardreader.java.utils.Constants.IS_DELETED;
import static com.huawei.cardreader.java.utils.Constants.IS_DELETED_FROM_PROFILEDETAILS;
import static com.huawei.cardreader.java.utils.Constants.QRCODEIMAGE;

/**
 * The type Contatct list adapter.
 */
public class GeneralcardListAdapter extends RecyclerView.Adapter
        <GeneralcardListAdapter.ViewHolder> {
    private List<QRcodmodel> mListData;
    private Context mContext;
    private AdapterCallback mAdapterCallback;
    private AlertDialog.Builder mBuilder;


    /**
     * Instantiates a new Contatct list adapter.
     *
     * @param mainActivity   the main activity
     * @param qRcodmodelList the q rcodmodel list
     */
    public GeneralcardListAdapter(ScannedCardListActivity mainActivity, List<QRcodmodel>
            qRcodmodelList) {
        this.mListData = qRcodmodelList;
        this.mContext = mainActivity;
        mBuilder = new AlertDialog.Builder(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactListItemBinding binding = DataBindingUtil.inflate
                (LayoutInflater.from
                                (parent.getContext()),
                        R.layout.contact_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final QRcodmodel qRcodmodel = mListData.get(position);
        holder.itemRowBinding.cardtypeTextView.setText
                (qRcodmodel.getBusinesscardUserDetailsList().get(position).getCardType());
        holder.itemRowBinding.categoryTextView.setText(Constants.GENERALCARD);
        String maskPhno;
        if (qRcodmodel.getBusinesscardUserDetailsList().get
                (position).getCardType().equalsIgnoreCase(BUSINESS_CARD)) {
            if (qRcodmodel.getBusinesscardUserDetailsList().get(position).getMobileno() != null
                    && qRcodmodel.getBusinesscardUserDetailsList
                    ().get(position).getMobileno().length() > 0) {
                maskPhno = maskCardNumber(DataConverter.deCodeString
                        (qRcodmodel.getBusinesscardUserDetailsList().get
                        (position).getMobileno()), Constants.PHONENO_MASK);
            } else {
                maskPhno = Constants.DEFAULT_MASK;
            }
            String name = maskCardNumber(DataConverter.deCodeString
                    (qRcodmodel.getBusinesscardUserDetailsList().get
                    (position).getName()), Constants.CARD_MASK);
            holder.itemRowBinding.nameTextView.setText(name);
            holder.itemRowBinding.imgCard.setImageBitmap(DataConverter.stringToBitMap
                    (qRcodmodel.getBusinesscardUserDetailsList().get(position).getImage()));
            holder.itemRowBinding.jobTextView.setText(DataConverter.deCodeString
                    (qRcodmodel.getBusinesscardUserDetailsList
                    ().get(position).getJobtitle()));
            holder.itemRowBinding.companyTextView.setText(DataConverter.deCodeString
                    (qRcodmodel.getBusinesscardUserDetailsList
                    ().get(position).getCompanyname()));
            holder.itemRowBinding.phnoTextView.setText(maskPhno);
        } else if (qRcodmodel.getBusinesscardUserDetailsList().get(position).getCardType
                ().equalsIgnoreCase(AADHARCARD)) {
            setAadharCardVisible(holder, qRcodmodel, position);
        } else {
            if (qRcodmodel.getBusinesscardUserDetailsList().get(position).getPannumber() != null
                    && qRcodmodel.getBusinesscardUserDetailsList().get
                    (position).getPannumber().length() > 0) {
                maskPhno = maskCardNumber(DataConverter.deCodeString
                        (qRcodmodel.getBusinesscardUserDetailsList().get
                        (position).getPannumber()), Constants.PHONENO_MASK);
            } else {
                maskPhno = Constants.DEFAULT_MASK;
            }
            String name = maskCardNumber(DataConverter.deCodeString
                    (qRcodmodel.getBusinesscardUserDetailsList().get
                    (position).getName()), Constants.CARD_MASK);
            setPanCardVisible(holder, maskPhno, name, qRcodmodel, position);
        }
        holder.itemRowBinding.liItems.setOnClickListener(view -> navigatetoAuthenticationPage
                (qRcodmodel, position));
        holder.itemRowBinding.imgDelete.setOnClickListener(view -> showalertDialog
                (qRcodmodel, holder, position));
    }

    private void setPanCardVisible(ViewHolder holder, String maskPhno, String name, QRcodmodel
            qRcodmodel, int position) {
        holder.itemRowBinding.nameTextView.setText(name);
        holder.itemRowBinding.imgCard.setImageBitmap(DataConverter.stringToBitMap
                (qRcodmodel.getBusinesscardUserDetailsList().get(position).getImage()));
        holder.itemRowBinding.phnoTextView.setText(maskPhno);
        holder.itemRowBinding.jobLabel.setVisibility(View.GONE);
        holder.itemRowBinding.companyLabel.setVisibility(View.GONE);
        holder.itemRowBinding.jobTextView.setVisibility(View.GONE);
        holder.itemRowBinding.companyTextView.setVisibility(View.GONE);
        holder.itemRowBinding.phnoLabel.setText(Constants.PANCARD);
        holder.itemRowBinding.addcontact.setVisibility(View.GONE);
    }

    private void setAadharCardVisible(ViewHolder holder, QRcodmodel qRcodmodel, int position) {
        String maskPhno;
        if (qRcodmodel.getBusinesscardUserDetailsList().get(position).getAadharid() != null
                && qRcodmodel.getBusinesscardUserDetailsList().get
                (position).getAadharid().length() > 0) {
            maskPhno = maskCardNumber(DataConverter.deCodeString
                    (qRcodmodel.getBusinesscardUserDetailsList().get
                    (position).getAadharid()), Constants.PHONENO_MASK);
        } else {
            maskPhno = Constants.DEFAULT_MASK;
        }
        String name = maskCardNumber(DataConverter.deCodeString
                (qRcodmodel.getBusinesscardUserDetailsList().get
                (position).getName()), Constants.CARD_MASK);
        holder.itemRowBinding.nameTextView.setText(name);
        holder.itemRowBinding.imgCard.setImageBitmap(DataConverter.stringToBitMap
                (qRcodmodel.getBusinesscardUserDetailsList
                        ().get(position).getImage()));
        holder.itemRowBinding.phnoTextView.setText(maskPhno);
        holder.itemRowBinding.jobLabel.setVisibility(View.GONE);
        holder.itemRowBinding.companyLabel.setVisibility(View.GONE);
        holder.itemRowBinding.jobTextView.setVisibility(View.GONE);
        holder.itemRowBinding.companyTextView.setVisibility(View.GONE);
        holder.itemRowBinding.phnoLabel.setText(R.string.aadharcard_number);
        holder.itemRowBinding.addcontact.setVisibility(View.GONE);
    }

    private void navigatetoAuthenticationPage(QRcodmodel qRcodmodel, int position) {
        IS_DELETED_FROM_PROFILEDETAILS = false;
        Intent intent = new Intent(mContext, AuthenticationPage.class);
        intent.putExtra(ID, qRcodmodel.getBusinesscardUserDetailsList
                ().get(position).getId());
        intent.putExtra(QRCODEIMAGE, qRcodmodel.getBitmap());
        intent.putExtra(Constants.QRCODECARDTYPE,qRcodmodel.getBusinesscardUserDetailsList
                ().get(position).getCardType());
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    /**
     * The type View holder.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The Item row binding.
         */
        public ContactListItemBinding itemRowBinding;

        /**
         * Instantiates a new View holder.
         *
         * @param itemBinding the item view
         */
        public ViewHolder(ContactListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemRowBinding = itemBinding;
        }
    }

    private void showalertDialog(QRcodmodel qRcodmodel, ViewHolder holder, int position) {
        mBuilder.setMessage(R.string.delete_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    mAdapterCallback.onMethodCallback(qRcodmodel.getBusinesscardUserDetailsList
                            ().get(position).getId());
                    int actualPosition = holder.getAdapterPosition();
                    mListData.remove(actualPosition);
                    notifyItemRemoved(actualPosition);
                    Toast.makeText(mContext, R.string.Deleted_the_card, Toast.LENGTH_SHORT).show();
                    IS_DELETED = mListData.isEmpty();
                })
                .setNegativeButton(R.string.no, (dialog, id) -> dialog.cancel());
        AlertDialog alert = mBuilder.create();
        alert.setTitle("");
        alert.show();
    }

    /**
     * Mask card number string.
     *
     * @param cardNumber the card number
     * @param mask       the mask
     * @return the string
     */
    public static String maskCardNumber(String cardNumber, String mask) {
        int index = 0;
        StringBuilder maskedNumber = new StringBuilder();
        for (int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            if (c == '#') {
                maskedNumber.append(cardNumber.charAt(index));
                index++;
            } else if (c == 'x') {
                maskedNumber.append(c);
                index++;
            } else {
                maskedNumber.append(c);
            }
        }

        // return the masked number
        return maskedNumber.toString();
    }

    /**
     * The interface Adapter callback.
     */
    public interface AdapterCallback {
        /**
         * On method callback.
         *
         * @param id the id
         */
        void onMethodCallback(int id);
    }

    /**
     * Sets adapter callback.
     *
     * @param mAdapterCallback the m adapter callback
     */
    public void setmAdapterCallback(AdapterCallback mAdapterCallback) {
        this.mAdapterCallback = mAdapterCallback;
    }
}
