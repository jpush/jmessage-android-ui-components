package cn.jmessage.android.uikit.chatting;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import cn.jmessage.android.uikit.chatting.utils.IdHelper;
import cn.jmessage.android.uikit.chatting.utils.SharePreferenceManager;

import cn.jpush.im.android.api.model.Conversation;

public class ChatView extends RelativeLayout {

    private LinearLayout mBackground;
    private TextView mTitle;
    private TableLayout mMoreMenuTl;
    private DropDownListView mChatListView;
    private RecordVoiceButton mVoiceBtn;
    public EditText mChatInputEt;
    private ImageButton mReturnIb;
    private ImageButton mSwitchIb;
    private ImageButton mAddFileIb;
    private ImageButton mTakePhotoIb;
    private ImageButton mPickPictureIb;
    private Button mSendMsgBtn;
    Context mContext;
    private OnSizeChangedListener mListener;
    private OnKeyBoardChangeListener mKeyboardListener;

    public static final byte KEYBOARD_STATE_SHOW = -3;
    public static final byte KEYBOARD_STATE_HIDE = -2;
    public static final byte KEYBOARD_STATE_INIT = -1;
    private boolean mHasInit;
    private boolean mHasKeybord;
    private int mHeight;

    public ChatView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }


    public void initModule() {
        mTitle = (TextView) findViewById(IdHelper.getViewID(mContext, "jmui_title"));
        mReturnIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_return_btn"));
        mChatListView = (DropDownListView) findViewById(IdHelper.getViewID(mContext, "jmui_chat_list"));
        mVoiceBtn = (RecordVoiceButton) findViewById(IdHelper.getViewID(mContext, "jmui_voice_btn"));
        mChatInputEt = (EditText) findViewById(IdHelper.getViewID(mContext, "jmui_chat_input_et"));
        mSwitchIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_switch_voice_ib"));
        mAddFileIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_add_file_btn"));
        mTakePhotoIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_pick_from_camera_btn"));
        mPickPictureIb = (ImageButton) findViewById(IdHelper.getViewID(mContext, "jmui_pick_from_local_btn"));
        mSendMsgBtn = (Button) findViewById(IdHelper.getViewID(mContext, "jmui_send_msg_btn"));
        mBackground = (LinearLayout) findViewById(IdHelper.getViewID(mContext, "jmui_chat_background"));
        mMoreMenuTl = (TableLayout) findViewById(IdHelper.getViewID(mContext, "jmui_more_menu_tl"));
        mBackground.requestFocus();
        mChatInputEt.addTextChangedListener(watcher);
        mChatInputEt.setOnFocusChangeListener(listener);
        mChatInputEt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mChatInputEt.setSingleLine(false);
        mChatInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dismissMoreMenu();
                    Log.i("ChatView", "dismissMoreMenu()----------");
                }
                return false;
            }
        });
        mChatInputEt.setMaxLines(4);
        setMoreMenuHeight();
    }

    public void setMoreMenuHeight() {
        int softKeyboardHeight = SharePreferenceManager.getCachedKeyboardHeight();
        if(softKeyboardHeight > 0){
            mMoreMenuTl.setLayoutParams(new LinearLayout
                    .LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, softKeyboardHeight));
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mListener != null){
            mListener.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mHasInit) {
            mHasInit = true;
            mHeight = b;
            if (mKeyboardListener != null) {
                mKeyboardListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
            }
        } else {
            mKeyboardListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
            mHeight = mHeight < b ? b : mHeight;
        }
        if (mHasInit && mHeight > b) {
            mHasKeybord = true;
            if (mKeyboardListener != null) {
                mKeyboardListener.onKeyBoardStateChange(KEYBOARD_STATE_SHOW);
            }
        }
        if (mHasInit && mHasKeybord && mHeight == b) {
            mHasKeybord = false;
            if (mKeyboardListener != null) {
                mKeyboardListener.onKeyBoardStateChange(KEYBOARD_STATE_HIDE);
            }
        }
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        this.mListener = listener;
    }

    public void setChatTitle(String nickname) {
        mTitle.setText(nickname);
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    public interface OnKeyBoardChangeListener {
        void onKeyBoardStateChange(int state);
    }

    public void setOnKbdStateListener(OnKeyBoardChangeListener listener) {
        mKeyboardListener = listener;
    }

    private TextWatcher watcher = new TextWatcher() {
        private CharSequence temp = "";
        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub
            if (temp.length() > 0) {
                mAddFileIb.setVisibility(View.GONE);
                mSendMsgBtn.setVisibility(View.VISIBLE);
            }else {
                mAddFileIb.setVisibility(View.VISIBLE);
                mSendMsgBtn.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            temp = s;
        }

    };

    public void focusToInput(boolean inputFocus) {
        if (inputFocus) {
            mChatInputEt.requestFocus();
            Log.i("ChatView", "show softInput");
        } else {
            mAddFileIb.requestFocusFromTouch();
        }
    }

    OnFocusChangeListener listener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Log.i("ChatView", "Input focus");
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dismissMoreMenu();
                    }
                });
            }
        }
    };

    public void setListeners(OnClickListener onClickListener) {
        mReturnIb.setOnClickListener(onClickListener);
        mChatInputEt.setOnClickListener(onClickListener);
        mSendMsgBtn.setOnClickListener(onClickListener);
        mSwitchIb.setOnClickListener(onClickListener);
        mVoiceBtn.setOnClickListener(onClickListener);
        mAddFileIb.setOnClickListener(onClickListener);
        mTakePhotoIb.setOnClickListener(onClickListener);
        mPickPictureIb.setOnClickListener(onClickListener);
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mChatListView.setOnTouchListener(listener);
        mChatInputEt.setOnTouchListener(listener);
    }

    public void setChatListAdapter(MsgListAdapter adapter) {
        mChatListView.setAdapter(adapter);
        setToBottom();
    }

    //如果是文字输入
    public void isKeyBoard() {
        mSwitchIb.setBackgroundResource(IdHelper.getDrawable(mContext, "jmui_voice"));
        mChatInputEt.setVisibility(View.VISIBLE);
        mVoiceBtn.setVisibility(View.GONE);
        if (mChatInputEt.getText().length() > 0) {
            mSendMsgBtn.setVisibility(View.VISIBLE);
            mAddFileIb.setVisibility(View.GONE);
        }else {
            mSendMsgBtn.setVisibility(View.GONE);
            mAddFileIb.setVisibility(View.VISIBLE);
        }
    }

    //语音输入
    public void notKeyBoard(Conversation conv, MsgListAdapter adapter) {
        mChatInputEt.setVisibility(View.GONE);
        mSwitchIb.setBackgroundResource(IdHelper.getDrawable(mContext, "jmui_keyboard"));
        mVoiceBtn.setVisibility(View.VISIBLE);
        mVoiceBtn.initConv(conv, adapter);
        mSendMsgBtn.setVisibility(View.GONE);
        mAddFileIb.setVisibility(View.VISIBLE);
    }

    public String getChatInput() {
        return mChatInputEt.getText().toString();
    }

    public void clearInput() {
        mChatInputEt.setText("");
    }

    public void setToBottom() {
        mChatListView.post(new Runnable() {
            @Override
            public void run() {
                mChatListView.setSelection(mChatListView.getBottom());
            }
        });
    }

    public EditText getInputView() {
        return mChatInputEt;
    }

    public TableLayout getMoreMenu() {
        return mMoreMenuTl;
    }

    public void showMoreMenu() {
        mMoreMenuTl.setVisibility(View.VISIBLE);
    }

    public void invisibleMoreMenu() {
        mMoreMenuTl.setVisibility(INVISIBLE);
    }

    public void dismissMoreMenu() {
        mMoreMenuTl.setVisibility(View.GONE);
    }

    public void dismissRecordDialog() {
        mVoiceBtn.dismissDialog();
    }

    public void releaseRecorder() {
        mVoiceBtn.releaseRecorder();
    }

    public DropDownListView getListView() {
        return mChatListView;
    }

}
