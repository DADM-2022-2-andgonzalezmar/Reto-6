package co.unal.edu.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.unal.edu.tictactoe.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AndroidTicTacToeActivity mGame;
    private Button mBoardButtons[] = new Button[AndroidTicTacToeActivity.BOARD_SIZE];
    private ActivityMainBinding binding;

    private TextView mInfoTextView;

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