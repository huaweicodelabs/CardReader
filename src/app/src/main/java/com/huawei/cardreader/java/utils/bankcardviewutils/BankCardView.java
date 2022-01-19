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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.huawei.cardreader.R;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardNumberFormat.ALL_DIGITS_FORMAT;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardNumberFormat.MASKED_ALL_FORMAT;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardNumberFormat.MASKED_ALL_BUT_LAST_FOUR_FORMAT;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardNumberFormat.ONLY_LAST_FOUR_FORMAT;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardType.BANKCARD_AMERICAN_EXPRESS;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardType.AUTO;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardType.BANK_CARD_VISA;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardType.BANKCARD_DISCOVER;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardType.BANKCARD_MASTERCARD;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardType.PATTERN_FOR_AMERICAN_EXPRESS;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardType.PATTERN_FOR_DISCOVER;
import static com.huawei.cardreader.java.utils.bankcardviewutils.BankCardType.PATTERN_FOR_MASTER_CARD;

/**
 * The type Bank Credit card view.
 */
public class BankCardView extends RelativeLayout {
    private static final int CARD_FRONT = 0;
    private static final int CARD_BACK = 1;
    private static final boolean DEBUG = false;
    private final Context mContext;
    private String mBankCardNumber;
    private String mBankCardName;
    private String mBankCardExpiryDate;
    private String mBankCardCvv;
    private String mFontStylePath;
    private int mBankCardNumberTextColor = Color.WHITE;
    private int mBankCardNumberFormat = ALL_DIGITS_FORMAT;
    private int mBankCardNameTextColor = Color.WHITE;
    private int mBankExpiryDateTextColor = Color.WHITE;
    private int mBankCvvTextColor = Color.BLACK;
    private int mBankValidTillTextColor = Color.WHITE;
    @BankCreditCardType
    private int mBankCardType = BANK_CARD_VISA;
    private int mCardBrandLogo;
    private int mBankcardSide = CARD_FRONT;
    private boolean mPutBankCardChip;
    private boolean mIsFieldEditable;
    private boolean mIsBankCardNumberEditable;
    private boolean mIsBankCardNameEditable;
    private boolean mIsBankCardExpiryDateEditable;
    private boolean mIsBankCardCvvEditable;
    private boolean mIsBankCardFlippable;
    private int mTextHintColor = Color.WHITE;
    private int mCardCvvHintColor = Color.WHITE;
    @DrawableRes
    private int mBankCardFrontBackground;
    @DrawableRes
    private int mBankCardBackBackground;
    private Typeface mBankCreditCardTypeFace;
    private ImageButton mBankFlipBtn;
    private EditText mBankCardNumberView;
    private EditText mBankCardNameView;
    private EditText mBankCardExpiryDateView;
    private EditText mBankCardCvvView;
    private ImageView mBankCardTypeView;
    private ImageView mBrandLogoImageView;
    private ImageView mBankCardChipView;
    private TextView mValidTillTextView;
    private View mStripeView;
    private View mCardAuthorizedSig;
    private View mBankCardSignature;

    /**
     * Instantiates a new Credit card view.
     *
     * @param context the context
     */
    public BankCardView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Credit card view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public BankCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (context != null) {
            this.mContext = context;
        } else {
            this.mContext = getContext();
        }

        initView();
        loadBankCardAttributes(attrs);
        initDefaultsMethod();
        addComponentListeners();
    }

    /**
     * Initialize various views and variables
     */
    private void initView() {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        inflater.inflate(R.layout.creditcardview, this, true);

        mBankCardNumberView = findViewById(R.id.card_number);
        mBankCardNameView =  findViewById(R.id.card_name);
        mBankCardTypeView =  findViewById(R.id.card_logo);
        mBrandLogoImageView =  findViewById(R.id.brand_logo);
        mBankCardChipView =  findViewById(R.id.chip);
        mValidTillTextView =  findViewById(R.id.valid_till);
        mBankCardExpiryDateView =  findViewById(R.id.expiry_date);
        mBankFlipBtn = findViewById(R.id.flip_btn);
        mBankCardCvvView = findViewById(R.id.cvv_et);
        mStripeView = findViewById(R.id.stripe);
        mCardAuthorizedSig = findViewById(R.id.authorized_sig_tv);
        mBankCardSignature = findViewById(R.id.signature);
    }

    private void loadBankCardAttributes(@Nullable AttributeSet attrs) {
        final TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs,
                R.styleable.BankCreditCardView, 0, 0);

        try {
            mBankCardNumber = a.getString(R.styleable.BankCreditCardView_cardNumber);
            mBankCardName = a.getString(R.styleable.BankCreditCardView_cardName);
            mBankCardExpiryDate = a.getString(R.styleable.BankCreditCardView_expiryDate);
            mBankCardNumberTextColor = a.getColor(R.styleable.BankCreditCardView_cardNumberTextColor,
                    Color.WHITE);
            mBankCardNumberFormat = a.getInt(R.styleable.BankCreditCardView_cardNumberFormat, 0);
            mBankExpiryDateTextColor = a.getColor(R.styleable.BankCreditCardView_cardNumberTextColor,
                    Color.WHITE);
            mBankExpiryDateTextColor = a.getColor(R.styleable.BankCreditCardView_expiryDateTextColor,
                    Color.WHITE);
            mBankCvvTextColor = a.getColor(R.styleable.BankCreditCardView_cvvTextColor,
                    Color.BLACK);
            mBankValidTillTextColor = a.getColor(R.styleable.BankCreditCardView_validTillTextColor,
                    Color.WHITE);

            mBankCardType = a.getInt(R.styleable.BankCreditCardView_type, BANK_CARD_VISA);
            mCardBrandLogo = a.getResourceId(R.styleable.BankCreditCardView_brandLogo, 0);
            mPutBankCardChip = a.getBoolean(R.styleable.BankCreditCardView_putChip, false);
            mIsFieldEditable = a.getBoolean(R.styleable.BankCreditCardView_isEditable, false);

            mIsBankCardNameEditable = a.getBoolean(R.styleable.BankCreditCardView_isCardNameEditable,
                    mIsFieldEditable);
            mIsBankCardNumberEditable = a.getBoolean(R.styleable.BankCreditCardView_isCardNumberEditable,
                    mIsFieldEditable);
            mIsBankCardExpiryDateEditable = a.getBoolean(R.styleable.BankCreditCardView_isExpiryDateEditable,
                    mIsFieldEditable);
            mIsBankCardCvvEditable = a.getBoolean(R.styleable.BankCreditCardView_isCvvEditable, mIsFieldEditable);
            mTextHintColor = a.getColor(R.styleable.BankCreditCardView_hintTextColor, Color.WHITE);
            mIsBankCardFlippable = a.getBoolean(R.styleable.BankCreditCardView_isFlippable, mIsBankCardFlippable);
            mBankCardCvv = a.getString(R.styleable.BankCreditCardView_cvv);
            mBankCardFrontBackground = a.getResourceId(R.styleable.BankCreditCardView_cardFrontBackground,
                    R.drawable.cardbackground_sky);
            mBankCardBackBackground = a.getResourceId(R.styleable.BankCreditCardView_cardBackBackground,
                    R.drawable.cardbackground_canvas);
            mFontStylePath = a.getString(R.styleable.BankCreditCardView_fontPath);

        } finally {
            a.recycle();
        }
    }

    private void initDefaultsMethod() {
        setBackgroundResource(mBankCardFrontBackground);
        if (TextUtils.isEmpty(mFontStylePath)) {
            mFontStylePath = mContext.getString(R.string.font_path);
        }

        if (!isInEditMode()) {
            mBankCreditCardTypeFace = Typeface.createFromAsset(mContext.getAssets(), mFontStylePath);
        }

        if (TextUtils.isEmpty(mFontStylePath)) {
            mFontStylePath = mContext.getString(R.string.font_path);
        }

        if (!isInEditMode()) {
            mBankCreditCardTypeFace = Typeface.createFromAsset(mContext.getAssets(), mFontStylePath);
        }

        if (!mIsFieldEditable) {
            mBankCardNumberView.setEnabled(false);
            mBankCardNameView.setEnabled(false);
            mBankCardExpiryDateView.setEnabled(false);
            mBankCardCvvView.setEnabled(false);
        } else {
            mBankCardNumberView.setHint(R.string.card_number_hint);
            mBankCardNumberView.setHintTextColor(mTextHintColor);

            mBankCardNameView.setHint(R.string.card_name_hint);
            mBankCardNameView.setHintTextColor(mTextHintColor);

            mBankCardExpiryDateView.setHint(R.string.expiry_date_hint);
            mBankCardExpiryDateView.setHintTextColor(mTextHintColor);

            mBankCardCvvView.setHint(R.string.cvv_hint);
            mBankCardCvvView.setHintTextColor(mBankCvvTextColor);
        }

        if (mIsBankCardNameEditable != mIsFieldEditable) {
            if (mIsBankCardNameEditable) {
                mBankCardNameView.setHint(R.string.card_name_hint);
                mBankCardNameView.setHintTextColor(mTextHintColor);
            } else {
                mBankCardNameView.setHint("");
            }
            mBankCardNameView.setEnabled(mIsBankCardNameEditable);
        }

        if (mIsBankCardNumberEditable != mIsFieldEditable) {
            if (mIsBankCardNumberEditable) {
                mBankCardNumberView.setHint(R.string.card_number_hint);
                mBankCardNumberView.setHintTextColor(mTextHintColor);
            } else {
                mBankCardNumberView.setHint("");
            }
            mBankCardNumberView.setEnabled(mIsBankCardNumberEditable);
        }

        if (mIsBankCardExpiryDateEditable != mIsFieldEditable) {
            if (mIsBankCardExpiryDateEditable) {
                mBankCardExpiryDateView.setHint(R.string.expiry_date_hint);
                mBankCardExpiryDateView.setHintTextColor(mTextHintColor);
            } else {
                mBankCardExpiryDateView.setHint("");
            }
            mBankCardExpiryDateView.setEnabled(mIsBankCardExpiryDateEditable);
        }

        if (!TextUtils.isEmpty(mBankCardNumber)) {
            mBankCardNumberView.setText(getBankCardFormattedCardNumber(addSpaceToBankCardNumber()));
        }

        mBankCardNumberView.setTextColor(mBankCardNumberTextColor);

        if (!isInEditMode()) {
            mBankCardNumberView.setTypeface(mBankCreditCardTypeFace);
        }

        if (!TextUtils.isEmpty(mBankCardName)) {
            mBankCardNameView.setText(mBankCardName.toUpperCase(Locale.ROOT));
        }

        mBankCardNameView.setFilters(new InputFilter[]{
                new InputFilter.AllCaps()
        });

        mBankCardNameView.setTextColor(mBankCardNumberTextColor);

        if (!isInEditMode()) {
            mBankCardNameView.setTypeface(mBankCreditCardTypeFace);
        }

        mBankCardTypeView.setBackgroundResource(getBankCardLogo());

        if (mCardBrandLogo != 0) {
            mBrandLogoImageView.setBackgroundResource(mCardBrandLogo);
        }
        if (mPutBankCardChip) {
            mBankCardChipView.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(mBankCardExpiryDate)) {
            mBankCardExpiryDateView.setText(mBankCardExpiryDate);
        }

        mBankCardExpiryDateView.setTextColor(mBankExpiryDateTextColor);

        if (!isInEditMode()) {
            mBankCardExpiryDateView.setTypeface(mBankCreditCardTypeFace);
        }

        mValidTillTextView.setTextColor(mBankValidTillTextColor);

        if (!TextUtils.isEmpty(mBankCardCvv)) {
            mBankCardCvvView.setText(mBankCardCvv);
        }

        mBankCardCvvView.setTextColor(mBankCvvTextColor);

        if (!isInEditMode()) {
            mBankCardCvvView.setTypeface(mBankCreditCardTypeFace);
        }

        if (mIsBankCardCvvEditable != mIsFieldEditable) {
            if (mIsBankCardCvvEditable) {
                mBankCardCvvView.setHint(R.string.cvv_hint);
                mBankCardCvvView.setHintTextColor(mCardCvvHintColor);
            } else {
                mBankCardCvvView.setHint("");
            }

            mBankCardCvvView.setEnabled(mIsBankCardCvvEditable);
        }
        if (mIsBankCardFlippable) {
            mBankFlipBtn.setVisibility(View.VISIBLE);
        }
        mBankFlipBtn.setEnabled(mIsBankCardFlippable);
    }

    private void addComponentListeners() {
        mBankCardNumberView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBankCardType = AUTO;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBankCardNumberView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    if (!TextUtils.isEmpty(mBankCardNumber) && mBankCardNumber.length() > 12) {

                        if (mBankCardType == AUTO) {
                            mBankCardTypeView.setBackgroundResource(getBankCardLogo());
                        }

                        mBankCardNumberView.setText(getBankCardFormattedCardNumber(addSpaceToBankCardNumber()));
                    }
                }
            }
        });
        mBankCardNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mBankCardName = s.toString().toUpperCase(Locale.ROOT);
            }
        });
        mBankCardExpiryDateView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mBankCardExpiryDate = s.toString();
            }
        });


    }



    private void showBankFrontView() {
        mBankCardNumberView.setVisibility(View.VISIBLE);
        mBankCardNameView.setVisibility(View.VISIBLE);
        mBankCardTypeView.setVisibility(View.VISIBLE);
        mBrandLogoImageView.setVisibility(View.VISIBLE);
        if (mPutBankCardChip) {
            mBankCardChipView.setVisibility(View.VISIBLE);
        }
        mValidTillTextView.setVisibility(View.VISIBLE);
        mBankCardExpiryDateView.setVisibility(View.VISIBLE);
    }

    private void hideBankFrontView() {
        mBankCardNumberView.setVisibility(View.GONE);
        mBankCardNameView.setVisibility(View.GONE);
        mBankCardTypeView.setVisibility(View.GONE);
        mBrandLogoImageView.setVisibility(View.GONE);
        mBankCardChipView.setVisibility(View.GONE);
        mValidTillTextView.setVisibility(View.GONE);
        mBankCardExpiryDateView.setVisibility(View.GONE);
    }

    private void showBankBackView() {
        mStripeView.setVisibility(View.VISIBLE);
        mCardAuthorizedSig.setVisibility(View.VISIBLE);
        mBankCardSignature.setVisibility(View.VISIBLE);
        mBankCardCvvView.setVisibility(View.VISIBLE);
    }

    private void hideBankBackView() {
        mStripeView.setVisibility(View.GONE);
        mCardAuthorizedSig.setVisibility(View.GONE);
        mBankCardSignature.setVisibility(View.GONE);
        mBankCardCvvView.setVisibility(View.GONE);
    }

    private void redrawCardViews() {
        invalidate();
        requestLayout();
    }

    /**
     * Sets card number.
     *
     * @param cardNumber the card number
     */
    public void setBankCardNumber(String cardNumber) {
        if (cardNumber == null) {
            throw new NullPointerException(mContext.getString(R.string.card_number_cannot_benull));
        }
        this.mBankCardNumber = cardNumber.replaceAll("\\s+", "");
        this.mBankCardNumberView.setText(addSpaceToBankCardNumber());
        redrawCardViews();
    }
    /**
     * Sets card name.
     *
     * @param cardName the card name
     */
    public void setBankCardName(String cardName) {
        if (cardName == null) {
            throw new NullPointerException(mContext.getString(R.string.card_number_cannot_benull));
        }
        this.mBankCardName = cardName.toUpperCase(Locale.ROOT);
        this.mBankCardNameView.setText(mBankCardName);
        redrawCardViews();
    }
    /**
     * Gets card number format.
     *
     * @return the card number format
     */
    @BankCreditCardFormat
    public int getBankCardNumberFormat() {
        return mBankCardNumberFormat;
    }

    /**
     * Sets card number format.
     *
     * @param cardNumberFormat the card number format
     */
    public void setBankCardNumberFormat(@BankCreditCardFormat int cardNumberFormat) {
        if (cardNumberFormat < 0 | cardNumberFormat > 3) {
            throw new UnsupportedOperationException(mContext.getString(R.string.CardNumberFormat) + cardNumberFormat + "  " +
                    mContext.getString(R.string.isNotSupport) +
                    mContext.getString(R.string.unknown));
        }
        this.mBankCardNumberFormat = cardNumberFormat;
        this.mBankCardNumberView.setText(getBankCardFormattedCardNumber(mBankCardNumber));
        redrawCardViews();
    }
    /**
     * Sets expiry date.
     *
     * @param expiryDate the expiry date
     */
    public void setBankCardExpiryDate(String expiryDate) {
        this.mBankCardExpiryDate = expiryDate;
        this.mBankCardExpiryDateView.setText(mBankCardExpiryDate);
        redrawCardViews();
    }
    /**
     * Gets type.
     *
     * @return the type
     */
    @BankCreditCardType
    public int getType() {
        return mBankCardType;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setBankCardType(@BankCreditCardType int type) {
        if (type < 0 | type > 4) {
            throw new UnsupportedOperationException(mContext.getString(R.string.CardType) + type + mContext.getString(R.string.isNotSuppot) +
                    mContext.getString(R.string.ifKnown));
        }
        this.mBankCardType = type;
        this.mBankCardTypeView.setBackgroundResource(getBankCardLogo());
        redrawCardViews();
    }
    /**
     * Sets card back background.
     *
     * @param cardBackBackground the card back background
     */
    public void setBankCardBackBackground(@DrawableRes int cardBackBackground) {
        this.mBankCardBackBackground = cardBackBackground;
        setBackgroundResource(mBankCardBackBackground);
        redrawCardViews();
    }

    /**
     * Return the appropriate drawable resource based on the card type
     *  @return the int
     */
    @DrawableRes
    private int getBankCardLogo() {
        switch (mBankCardType) {
            case BANK_CARD_VISA:
                return R.drawable.visa1;

            case BANKCARD_MASTERCARD:
                return R.drawable.ic_mastercard;

            case BANKCARD_AMERICAN_EXPRESS:

            case BANKCARD_DISCOVER:

            case AUTO:
                return findBankCardType();

            default:
                throw new UnsupportedOperationException(mContext.getString(R.string.CardType) + mBankCardType + mContext.getString(R.string.isNotSuppot) +
                        mContext.getString(R.string.ifKnown));
        }
    }

    /**
     * Returns the formatted card number based on the user entered value for card number format
     *
     * @param cardNumber Card Number.
     *                    @return the String
     */
    private String getBankCardFormattedCardNumber(String cardNumber) {
        switch (getBankCardNumberFormat()) {
            case MASKED_ALL_BUT_LAST_FOUR_FORMAT:
                cardNumber = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
                break;
            case ONLY_LAST_FOUR_FORMAT:
                cardNumber = cardNumber.substring(cardNumber.length() - 4);
                break;
            case MASKED_ALL_FORMAT:
                cardNumber = "**** **** **** ****";
                break;
            case ALL_DIGITS_FORMAT:
                break;
            default:
                throw new UnsupportedOperationException(mContext.getString(R.string.creditCardFormat) + mBankCardNumberFormat +
                        mContext.getString(R.string.creditCardNotSupport));
        }
        return cardNumber;
    }


    /**
     * Find card type int.
     *
     * @return the int
     */
    @DrawableRes
    private int findBankCardType() {
        this.mBankCardType = BANK_CARD_VISA;
        if (!TextUtils.isEmpty(mBankCardNumber)) {
            final String cardNumber = mBankCardNumber.replaceAll("\\s+", "");

            if (Pattern.compile(PATTERN_FOR_MASTER_CARD).matcher(cardNumber).matches()) {
                this.mBankCardType = BANKCARD_MASTERCARD;
            } else if (Pattern.compile(PATTERN_FOR_AMERICAN_EXPRESS).matcher(cardNumber).matches()) {
                this.mBankCardType = BANKCARD_AMERICAN_EXPRESS;
            } else if (Pattern.compile(PATTERN_FOR_DISCOVER).matcher(cardNumber).matches()) {
                this.mBankCardType = BANKCARD_DISCOVER;
            }
        }
        return getBankCardLogo();
    }


    /**
     * Add space to card number string.
     *
     * @return the string
     */
    private String addSpaceToBankCardNumber() {
        final int splitBy = 4;
        final int length = mBankCardNumber.length();

        if (length % splitBy != 0 || length <= splitBy) {
            return mBankCardNumber;
        } else {
            final StringBuilder result = new StringBuilder();
            result.append(mBankCardNumber.substring(0, splitBy));
            for (int i = splitBy; i < length; i++) {
                if (i % splitBy == 0) {
                    result.append(" ");
                }
                result.append(mBankCardNumber.charAt(i));
            }
            return result.toString();
        }
    }





    /**
     * The interface Credit card type.
     */
    @IntDef({BANK_CARD_VISA, BANKCARD_MASTERCARD, BANKCARD_AMERICAN_EXPRESS, BANKCARD_DISCOVER, AUTO})
    public @interface BankCreditCardType {
    }

    /**
     * The interface Credit card format.
     */
    @IntDef({ALL_DIGITS_FORMAT, MASKED_ALL_BUT_LAST_FOUR_FORMAT, ONLY_LAST_FOUR_FORMAT, MASKED_ALL_FORMAT})
    public @interface BankCreditCardFormat {
    }
}
