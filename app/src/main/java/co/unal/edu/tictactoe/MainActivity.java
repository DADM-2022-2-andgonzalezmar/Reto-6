package co.unal.edu.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import co.unal.edu.tictactoe.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AndroidTicTacToeActivity mGame;
    private Button mBoardButtons[] = new Button[AndroidTicTacToeActivity.BOARD_SIZE];
    private ActivityMainBinding binding;

    private boolean mGameOver;
    private boolean humanTurn;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    private TextView mInfoTextView;

    private BoardView mBoardView;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    MediaPlayer mWinMediaPlayer;
    MediaPlayer mLoseMediaPlayer;
    MediaPlayer mTieMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mBoardView = binding.board;

        mInfoTextView = binding.information;
        mGame = new AndroidTicTacToeActivity();

        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        binding.buttonNewGame.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startNewGame();
                    }
                }
        );

        startNewGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_move);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer_move);
        mWinMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_win);
        mLoseMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_lose);
        mTieMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_tie);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
        mWinMediaPlayer.release();
        mLoseMediaPlayer.release();
        mTieMediaPlayer.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};
                int selected = 0;
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
                                AndroidTicTacToeActivity.DifficultyLevel difficulty = AndroidTicTacToeActivity.DifficultyLevel.Expert;
                                switch (item){
                                    case 0:
                                        difficulty = AndroidTicTacToeActivity.DifficultyLevel.Easy;
                                        break;
                                    case 1:
                                        difficulty = AndroidTicTacToeActivity.DifficultyLevel.Harder;
                                        break;
                                    case 2:
                                        difficulty = AndroidTicTacToeActivity.DifficultyLevel.Expert;
                                        break;
                                }
                                mGame.setDifficultyLevel(difficulty);
                                Toast.makeText(getApplicationContext(), levels[item], Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();
        mGameOver = false;
        humanTurn = true;
        mInfoTextView.setText(R.string.first_human);
    }

    public class ButtonClickListener implements View.OnClickListener{
        int location;

        public ButtonClickListener(int location) {
            this.location = location;

        }

        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled()) {
                setMove(AndroidTicTacToeActivity.HUMAN_PLAYER, location);
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(AndroidTicTacToeActivity.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }
                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1)
                    mInfoTextView.setText(R.string.result_tie);
                else if (winner == 2)
                    mInfoTextView.setText(R.string.result_human_wins);
                else
                    mInfoTextView.setText(R.string.result_computer_wins);
            }
        }
    }

    private boolean setMove(char player, int move) {
        if (mGame.setMove(player, move)) {
            mBoardView.invalidate();   // Redraw the board
            if(humanTurn)
                mHumanMediaPlayer.start();
            else
                mComputerMediaPlayer.start();
            checkForWinner();
            return true;
        }
        return false;
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){

        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int location = row * 3 + col;

            if (!mGameOver && humanTurn && setMove(AndroidTicTacToeActivity.HUMAN_PLAYER, location)){
                setMove(AndroidTicTacToeActivity.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    humanTurn = false;

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            int move = mGame.getComputerMove();
                            setMove(AndroidTicTacToeActivity.COMPUTER_PLAYER, move);
                            checkForWinner();
                            humanTurn = true;
                        }
                    }, 2000);

                }

            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private void checkForWinner() {

        int winner = mGame.checkForWinner();
        Handler handler = new Handler();

        if (winner == 0)
            mInfoTextView.setText(R.string.turn_human);
        else if (winner == 1) {
            mInfoTextView.setText(R.string.result_tie);
            mTieMediaPlayer.start();
            mGameOver = true;
            handler.postDelayed(new Runnable() {
                public void run() {
                    startNewGame();
                }
            }, 3000);
        } else if (winner == 2) {
            mInfoTextView.setText(R.string.result_human_wins);
            mWinMediaPlayer.start();
            mGameOver = true;
            handler.postDelayed(new Runnable() {
                public void run() {
                    startNewGame();
                }
            }, 3000);
        } else {
            mInfoTextView.setText(R.string.result_computer_wins);
            mLoseMediaPlayer.start();
            mGameOver = true;
            handler.postDelayed(new Runnable() {
                public void run() {
                    startNewGame();
                }
            }, 3000);
        }
    }
}