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
import com.huawei.cardreader.kotlin.utils.Constants.AADHARCARD
import com.huawei.cardreader.kotlin.utils.Constants.BUSINESS_CARD
import com.huawei.cardreader.kotlin.utils.Constants.ID
import com.huawei.cardreader.kotlin.utils.Constants.IS_DELETED
import com.huawei.cardreader.kotlin.utils.Constants.IS_DELETED_FROM_PROFILEDETAILS
import com.huawei.cardreader.kotlin.utils.Constants.QRCODEIMAGE
import com.huawei.cardreader.kotlin.utils.DataConverter

/**
 * The type Contatct list adapter.
 */
class GeneralcardListAdapter(
    mainActivity: ScannedCardListActivity,
    private val mListData: MutableList<QRcodmodel>
) : RecyclerView.Adapter<GeneralcardListAdapter.ViewHolder>() {
    private val mContext: Context
    private var mAdapterCallback: AdapterCallback? =
        null
    private val mBuilder: AlertDialog.Builder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ContactListItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.contact_list_item, parent, false
            )
        return ViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val qRcodmodel: QRcodmodel = mListData[position]
        holder.itemRowBinding.cardtypeTextView.text =
            qRcodmodel.businesscardUserDetailsList!![position].cardType
        holder.itemRowBinding.categoryTextView.text = Constants.GENERALCARD
        val maskPhno: String
        if (qRcodmodel.businesscardUserDetailsList!![position].cardType.equals(
                BUSINESS_CARD,
                ignoreCase = true
            )
        ) {
            maskPhno =
                if (qRcodmodel.businesscardUserDetailsList!![position].mobileno != null
                    && qRcodmodel.businesscardUserDetailsList!![position].mobileno!!.length > 0
                ) {
                    maskCardNumber(
                        DataConverter.deCodeString(
                            qRcodmodel.businesscardUserDetailsList!![position].mobileno.toString()
                        ),
                        Constants.PHONENO_MASK
                    )
                } else {
                    Constants.DEFAULT_MASK
                }
            val name =
                maskCardNumber(
                    DataConverter.deCodeString(
                        qRcodmodel.businesscardUserDetailsList!![position].name.toString()
                    ),
                    Constants.CARD_MASK
                )
            holder.itemRowBinding.nameTextView.text = name
            holder.itemRowBinding.imgCard.setImageBitmap(
                DataConverter.stringToBitMap(
                    qRcodmodel.businesscardUserDetailsList!![position].image
                )
            )
            holder.itemRowBinding.jobTextView.setText(
                DataConverter.deCodeString(
                    qRcodmodel.businesscardUserDetailsList!![position].jobtitle.toString()
                )
            )
            holder.itemRowBinding.companyTextView.setText(
                DataConverter.deCodeString(
                    qRcodmodel.businesscardUserDetailsList!![position].companyname.toString()
                )
            )
            holder.itemRowBinding.phnoTextView.text = maskPhno
        } else if (qRcodmodel.businesscardUserDetailsList!![position].cardType.equals(
                AADHARCARD,
                ignoreCase = true
            )
        ) {
            setAadharCardVisible(holder, qRcodmodel, position)
        } else {
            maskPhno =
                 if (qRcodmodel.businesscardUserDetailsList!![position].pannumber != null
                    && qRcodmodel.businesscardUserDetailsList!![position].pannumber!!.length > 0
                ) {
                    maskCardNumber(
                        DataConverter.deCodeString(
                            qRcodmodel.businesscardUserDetailsList!![position].pannumber.toString()
                        ),
                        Constants.PHONENO_MASK
                    )
                } else {
                    Constants.DEFAULT_MASK
                }
            val name =
                maskCardNumber(
                    DataConverter.deCodeString(
                        qRcodmodel.businesscardUserDetailsList!![position].name.toString()
                    ),
                    Constants.CARD_MASK
                )
            setPanCardVisible(holder, maskPhno, name, qRcodmodel, position)
        }
        holder.itemRowBinding.liItems.setOnClickListener { view: View? ->
            navigatetoAuthenticationPage(
                qRcodmodel,
                position
            )
        }
        holder.itemRowBinding.imgDelete.setOnClickListener { view: View? ->
            showalertDialog(
                qRcodmodel,
                holder,
                position
            )
        }
    }

    private fun setPanCardVisible(
        holder: ViewHolder,
        maskPhno: String,
        name: String,
        qRcodmodel: QRcodmodel,
        position: Int
    ) {
        holder.itemRowBinding.nameTextView.text = name
        holder.itemRowBinding.imgCard.setImageBitmap(
            DataConverter.stringToBitMap(
                qRcodmodel.businesscardUserDetailsList!![position].image
            )
        )
        holder.itemRowBinding.phnoTextView.text = maskPhno
        holder.itemRowBinding.jobLabel.visibility = View.GONE
        holder.itemRowBinding.companyLabel.visibility = View.GONE
        holder.itemRowBinding.jobTextView.visibility = View.GONE
        holder.itemRowBinding.companyTextView.visibility = View.GONE
        holder.itemRowBinding.phnoLabel.setText(Constants.PANCARD)
        holder.itemRowBinding.addcontact.visibility = View.GONE
    }

    private fun setAadharCardVisible(
        holder: ViewHolder,
        qRcodmodel: QRcodmodel,
        position: Int
    ) {
        val maskPhno: String
        maskPhno =
            if (qRcodmodel.businesscardUserDetailsList!![position].aadharid != null
                && qRcodmodel.businesscardUserDetailsList!![position].aadharid!!.length > 0
            ) {
                maskCardNumber(
                    DataConverter.deCodeString(
                        qRcodmodel.businesscardUserDetailsList!![position].aadharid.toString()
                    ),
                    Constants.PHONENO_MASK
                )
            } else {
                Constants.DEFAULT_MASK
            }
        val name =
            maskCardNumber(
                DataConverter.deCodeString(
                    qRcodmodel.businesscardUserDetailsList!![position].name.toString()
                ),
                Constants.CARD_MASK
            )
        holder.itemRowBinding.nameTextView.text = name
        holder.itemRowBinding.imgCard.setImageBitmap(
            DataConverter.stringToBitMap(
                qRcodmodel.businesscardUserDetailsList!![position].image
            )
        )
        holder.itemRowBinding.phnoTextView.text = maskPhno
        holder.itemRowBinding.jobLabel.visibility = View.GONE
        holder.itemRowBinding.companyLabel.visibility = View.GONE
        holder.itemRowBinding.jobTextView.visibility = View.GONE
        holder.itemRowBinding.companyTextView.visibility = View.GONE
        holder.itemRowBinding.phnoLabel.setText(R.string.aadharcard_number)
        holder.itemRowBinding.addcontact.visibility = View.GONE
    }

    private fun navigatetoAuthenticationPage(qRcodmodel: QRcodmodel, position: Int) {
        IS_DELETED_FROM_PROFILEDETAILS = false
        val intent = Intent(mContext, AuthenticationPage::class.java)
        intent.putExtra(ID, qRcodmodel.businesscardUserDetailsList!![position].id)
        intent.putExtra(QRCODEIMAGE, qRcodmodel.bitmap)
        intent.putExtra(Constants.QRCODECARDTYPE,qRcodmodel.businesscardUserDetailsList!![position].cardType)
        mContext.startActivity(intent)
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
     */(
        /**
         * The Item row binding.
         */
        var itemRowBinding: ContactListItemBinding
    ) :
        RecyclerView.ViewHolder(itemRowBinding.root)

    private fun showalertDialog(
        qRcodmodel: QRcodmodel,
        holder: ViewHolder,
        position: Int
    ) {
        mBuilder.setMessage(R.string.delete_dialog)
            .setCancelable(false)
            .setPositiveButton(
                R.string.yes
            ) { dialog: DialogInterface?, id: Int ->
                mAdapterCallback!!.onMethodCallback(
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

    /**
     * The interface Adapter callback.
     */
    interface AdapterCallback {
        /**
         * On method callback.
         *
         * @param id the id
         */
        fun onMethodCallback(id: Int)
    }

    /**
     * Sets adapter callback.
     *
     * @param mAdapterCallback the m adapter callback
     */
    fun setmAdapterCallback(mAdapterCallback: AdapterCallback?) {
        this.mAdapterCallback = mAdapterCallback
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
     * Instantiates a new Contatct list adapter.
     *
     * @param mainActivity   the main activity
     * @param qRcodmodelList the q rcodmodel list
     */
    init {

        mContext = mainActivity
        mBuilder = AlertDialog.Builder(mContext)
    }
}