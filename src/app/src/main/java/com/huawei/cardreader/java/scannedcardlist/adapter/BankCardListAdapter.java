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

import static com.huawei.cardreader.java.utils.Constants.ID;
import static com.huawei.cardreader.java.utils.Constants.IS_DELETED;
import static com.huawei.cardreader.java.utils.Constants.IS_DELETED_FROM_PROFILEDETAILS;
import static com.huawei.cardreader.java.utils.Constants.QRCODEIMAGE;

/**
 * The type Bank card list adapter.
 */
public class BankCardListAdapter extends RecyclerView.Adapter<BankCardListAdapter.ViewHolder> {
    private List<QRcodmodel> mListData;
    private Context mContext;
    private AlertDialog.Builder mBuilder;
    private BankAdapterCallback mBankAdapterCallback;

    /**
     * Instantiates a new Bank card list adapter.
     *
     * @param mainActivity   the main activity
     * @param qRcodmodelList the qrcodmodel list
     */
    public BankCardListAdapter(ScannedCardListActivity mainActivity, List<QRcodmodel> qRcodmodelList
    ) {
        this.mListData = qRcodmodelList;
        this.mContext = mainActivity;
        mBuilder = new AlertDialog.Builder(mContext);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactListItemBinding binding = DataBindingUtil.inflate
                (LayoutInflater.from(parent.getContext()), R.layout.contact_list_item,
                        parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final QRcodmodel qRcodmodel = mListData.get(position);

        if (qRcodmodel.getBusinesscardUserDetailsList().get(position).getCardType
                ().equalsIgnoreCase(Constants.BANKCARD)) {
            String maskPhno;
            if (qRcodmodel.getBusinesscardUserDetailsList().get(position).getAccountnumber() != null
                    && qRcodmodel.getBusinesscardUserDetailsList().get
                    (position).getAccountnumber().length() > 0) {
                maskPhno = maskCardNumber(DataConverter.deCodeString
                        (qRcodmodel.getBusinesscardUserDetailsList().get
                        (position).getAccountnumber()), Constants.PHONENO_MASK);
            } else {
                maskPhno = Constants.DEFAULT_MASK;
            }
            String name = maskCardNumber(DataConverter.deCodeString
                    (qRcodmodel.getBusinesscardUserDetailsList().get
                    (position).getName()), Constants.NAME_MASK);
            holder.itemRowBinding.nameTextView.setText(name);
            holder.itemRowBinding.cardtypeTextView.setText(qRcodmodel.getBusinesscardUserDetailsList
                    ().get(position).getCardType());
            holder.itemRowBinding.categoryTextView.setText(Constants.BANKCARD);
            holder.itemRowBinding.imgCard.setImageBitmap(DataConverter.stringToBitMap
                    (qRcodmodel.getBusinesscardUserDetailsList().get(position).getImage()));
            holder.itemRowBinding.phnoTextView.setText(maskPhno);
            holder.itemRowBinding.jobLabel.setVisibility(View.VISIBLE);
            holder.itemRowBinding.jobLabel.setText(R.string.expireDate);
            holder.itemRowBinding.jobTextView.setVisibility(View.VISIBLE);
            String expiry = maskCardNumber(DataConverter.deCodeString
                    (qRcodmodel.getBusinesscardUserDetailsList().get
                    (position).getExpirydate()), Constants.EXPIRY_MASK);
            holder.itemRowBinding.jobTextView.setText(expiry);
            holder.itemRowBinding.companyLabel.setVisibility(View.VISIBLE);
            holder.itemRowBinding.companyLabel.setText(R.string.type);
            holder.itemRowBinding.companyTextView.setVisibility(View.VISIBLE);
            holder.itemRowBinding.companyTextView.setText
                    (DataConverter.deCodeString(qRcodmodel.getBusinesscardUserDetailsList().get
                            (position).getBankorganization()));
            holder.itemRowBinding.phnoLabel.setText(R.string.accNumber);
        }
        holder.itemRowBinding.liItems.setOnClickListener(view -> {
            IS_DELETED_FROM_PROFILEDETAILS = false;
            Intent intent = new Intent(mContext, AuthenticationPage.class);
            intent.putExtra(ID, qRcodmodel.getBusinesscardUserDetailsList().get
                    (position).getId());
            intent.putExtra(QRCODEIMAGE, qRcodmodel.getBitmap());
            intent.putExtra(Constants.QRCODECARDTYPE,qRcodmodel.getBusinesscardUserDetailsList
                    ().get(position).getCardType());
            mContext.startActivity(intent);
        });
        holder.itemRowBinding.imgDelete.setOnClickListener(view -> showalertDialog
                (qRcodmodel, holder, position));
    }


    /**
     * Showalert dialog.
     *
     * @param qRcodmodel the q rcodmodel
     * @param holder     the holder
     * @param position   the position
     */
    public void showalertDialog(QRcodmodel qRcodmodel, ViewHolder holder, int position) {
        mBuilder.setMessage(R.string.delete_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    mBankAdapterCallback.onMethodCallback
                            (qRcodmodel.getBusinesscardUserDetailsList().get(position).getId());
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

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    /**
     * The type View holder.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ContactListItemBinding itemRowBinding;

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

    /**
     * The interface Bank adapter callback.
     */
    public interface BankAdapterCallback {
        /**
         * On bank method callback.
         *
         * @param id the id
         */
        void onMethodCallback(int id);
    }

    /**
     * Sets bank adapter callback.
     *
     * @param mBankAdapterCallback the m bank adapter callback
     */
    public void setmBankAdapterCallback(BankAdapterCallback mBankAdapterCallback) {
        this.mBankAdapterCallback = mBankAdapterCallback;
    }
}
