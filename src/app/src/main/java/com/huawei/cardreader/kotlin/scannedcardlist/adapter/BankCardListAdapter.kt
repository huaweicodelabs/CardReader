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

package com.huawei.cardreader.kotlin.scannedcardlist.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.huawei.cardreader.R
import com.huawei.cardreader.databinding.ContactListItemBinding
import com.huawei.cardreader.kotlin.scannedcardlist.activity.ScannedCardListActivity
import com.huawei.cardreader.kotlin.scannedcardlist.model.QRcodmodel
import com.huawei.cardreader.kotlin.userauthentication.activity.AuthenticationPage
import com.huawei.cardreader.kotlin.utils.Constants
import com.huawei.cardreader.kotlin.utils.Constants.IS_DELETED
import com.huawei.cardreader.kotlin.utils.Constants.IS_DELETED_FROM_PROFILEDETAILS
import com.huawei.cardreader.kotlin.utils.DataConverter.deCodeString
import com.huawei.cardreader.kotlin.utils.DataConverter.stringToBitMap

/**
 * The type Bank card list adapter.
 */
class BankCardListAdapter(
    mainActivity: ScannedCardListActivity, private val mListData: MutableList<QRcodmodel>
) : RecyclerView.Adapter<BankCardListAdapter.ViewHolder>() {
    private val mContext: Context
    private val mBuilder: AlertDialog.Builder
    private var mBankAdapterCallback: BankAdapterCallback? =
        null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BankCardListAdapter.ViewHolder {
        val binding: ContactListItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.contact_list_item,
                parent, false
            )
        return ViewHolder(binding)
    }

   override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val qRcodmodel = mListData[position]
        if (qRcodmodel.businesscardUserDetailsList!![position].cardType.equals(
                Constants.BANKCARD,
                ignoreCase = true
            )
        ) {
            val maskPhno: String
            maskPhno = if (qRcodmodel.businesscardUserDetailsList!![position].accountnumber != null
                && qRcodmodel.businesscardUserDetailsList!![position].accountnumber!!.length > 0
            ) {
                maskCardNumber(
                    deCodeString(
                        qRcodmodel.businesscardUserDetailsList!![position].accountnumber!!
                    ),
                    Constants.PHONENO_MASK
                )
            } else {
                Constants.DEFAULT_MASK
            }
            val name =
                maskCardNumber(
                    deCodeString(qRcodmodel.businesscardUserDetailsList!![position].name!!),
                    Constants.NAME_MASK
                )
            holder.itemRowBinding.nameTextView.text = name
            holder.itemRowBinding.cardtypeTextView.text =
                qRcodmodel.businesscardUserDetailsList!![position].cardType
            holder.itemRowBinding.categoryTextView.text = Constants.BANKCARD
            holder.itemRowBinding.imgCard.setImageBitmap(
                stringToBitMap(
                    qRcodmodel.businesscardUserDetailsList!![position].image
                )
            )
            holder.itemRowBinding.phnoTextView.text = maskPhno
            holder.itemRowBinding.jobLabel.visibility = View.VISIBLE
            holder.itemRowBinding.jobLabel.setText(R.string.expireDate)
            holder.itemRowBinding.jobTextView.visibility = View.VISIBLE
            val expiry =
                maskCardNumber(
                    deCodeString(qRcodmodel.businesscardUserDetailsList!![position].expirydate!!),
                    Constants.EXPIRY_MASK
                )
            holder.itemRowBinding.jobTextView.text = expiry
            holder.itemRowBinding.companyLabel.visibility = View.VISIBLE
            holder.itemRowBinding.companyLabel.setText(R.string.type)
            holder.itemRowBinding.companyTextView.visibility = View.VISIBLE
            holder.itemRowBinding.companyTextView.text = deCodeString(
                qRcodmodel.businesscardUserDetailsList!![position].bankorganization!!
            )
            holder.itemRowBinding.phnoLabel.setText(R.string.accNumber)
        }
        holder.itemRowBinding.liItems.setOnClickListener { view: View? ->
            IS_DELETED_FROM_PROFILEDETAILS = false
            val intent = Intent(mContext, AuthenticationPage::class.java)
            intent.putExtra(Constants.ID, qRcodmodel.businesscardUserDetailsList!![position].id)
            intent.putExtra(Constants.QRCODEIMAGE, qRcodmodel.bitmap)
            intent.putExtra(Constants.QRCODECARDTYPE,Constants.BANKCARD)
            mContext.startActivity(intent)
        }
        holder.itemRowBinding.imgDelete.setOnClickListener { view: View? ->
            showalertDialog(
                qRcodmodel,
                holder,
                position
            )
        }
    }

    /**
     * Showalert dialog.
     *
     * @param qRcodmodel the q rcodmodel
     * @param holder     the holder
     * @param position   the position
     */
    fun showalertDialog(
        qRcodmodel: QRcodmodel,
        holder: ViewHolder,
        position: Int
    ) {
        mBuilder.setMessage(R.string.delete_dialog)
            .setCancelable(false)
            .setPositiveButton(
                R.string.yes
            ) { dialog: DialogInterface?, id: Int ->
                mBankAdapterCallback!!.onMethodCallback(
                    qRcodmodel.businesscardUserDetailsList!![position].id
                )
                val actualPosition = holder.adapterPosition
                mListData.removeAt(actualPosition)
                notifyItemRemoved(actualPosition)
                Toast.makeText(mContext, R.string.Deleted_the_card, Toast.LENGTH_SHORT).show()
                IS_DELETED = mListData.isEmpty()
            }
            .setNegativeButton(
                R.string.no
            ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert = mBuilder.create()
        alert.setTitle("")
        alert.show()
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    /**
     * The type View holder.
     */
    class ViewHolder
    /**
     * Instantiates a new View holder.
     *
     * @param itemBinding the item view
     */(val itemRowBinding: ContactListItemBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root)

    /**
     * The interface Bank adapter callback.
     */
    interface BankAdapterCallback {
        /**
         * On bank method callback.
         *
         * @param id the id
         */
        fun onMethodCallback(id: Int)
    }

    /**
     * Sets bank adapter callback.
     *
     * @param mBankAdapterCallback the m bank adapter callback
     */
    fun setmBankAdapterCallback(mBankAdapterCallback: BankAdapterCallback?) {
        this.mBankAdapterCallback = mBankAdapterCallback
    }

    companion object {
        /**
         * Mask card number string.
         *
         * @param cardNumber the card number
         * @param mask       the mask
         * @return the string
         */
        fun maskCardNumber(cardNumber: String, mask: String): String {
            var index = 0
            val maskedNumber = StringBuilder()
            for (i in 0 until mask.length) {
                val c = mask[i]
                if (c == '#') {
                    maskedNumber.append(cardNumber[index])
                    index++
                } else if (c == 'x') {
                    maskedNumber.append(c)
                    index++
                } else {
                    maskedNumber.append(c)
                }
            }
            // return the masked number
            return maskedNumber.toString()
        }
    }

    /**
     * Instantiates a new Bank card list adapter.
     *
     * @param mainActivity   the main activity
     * @param qRcodmodelList the qrcodmodel list
     */
    init {
        mContext = mainActivity
        mBuilder = AlertDialog.Builder(mContext)
    }
}