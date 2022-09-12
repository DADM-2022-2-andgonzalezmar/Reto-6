package co.unal.edu.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import co.unal.edu.tictactoe.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AndroidTicTacToeActivity mGame;
    private Button mBoardButtons[] = new Button[AndroidTicTacToeActivity.BOARD_SIZE];
    private ActivityMainBinding binding;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    private TextView mInfoTextView;

    private BoardView mBoardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mBoardButtons[0] = binding.one;
        mBoardButtons[1] = binding.two;
        mBoardButtons[2] = binding.three;
        mBoardButtons[3] = binding.four;
        mBoardButtons[4] = binding.five;
        mBoardButtons[5] = binding.six;
        mBoardButtons[6] = binding.seven;
        mBoardButtons[7] = binding.eight;
        mBoardButtons[8] = binding.nine;

        mInfoTextView = binding.information;
        mGame = new AndroidTicTacToeActivity();

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
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
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

    private void setMove(char player, int move) {
        mGame.setMove(player, move);
        mBoardButtons[move].setEnabled(false);
        mBoardButtons[move].setText(String.valueOf(player));
        if (player == AndroidTicTacToeActivity.HUMAN_PLAYER)
            mBoardButtons[move].setTextColor(Color.rgb(0,200,0));
        else
            mBoardButtons[move].setTextColor(Color.rgb(200,0,0));

    }
}