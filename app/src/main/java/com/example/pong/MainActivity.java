package com.example.pong;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements JGameLib.GameEvent {
    JGameLib gameLib = null;
    int m = 34, n = 20;
    JGameLib.Card cardEdgeL;
    JGameLib.Card cardEdgeR;
    JGameLib.Card cardEdgeT;
    JGameLib.Card cardEdgeB;
    JGameLib.Card cardBall;
    JGameLib.Card cardRacket1;
    JGameLib.Card cardRacket2;
    JGameLib.Card cardScore1;
    JGameLib.Card cardScore2;
    int scoreMe = 0, scoreCom = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameLib = findViewById(R.id.gameLib);
        initGame();
    }

    @Override
    protected void onDestroy() {
        if(gameLib != null)
            gameLib.clearMemory();
        super.onDestroy();
    }

    private void initGame() {
        gameLib.setScreenGrid(n,m);
        gameLib.listener(this);
        gameLib.addCardColor(Color.rgb(10,10,100));
        cardEdgeL = gameLib.addCardColor(Color.rgb(255,255,255), 0,0,1,m);
        cardEdgeL.checkCollision();
        cardEdgeR = gameLib.addCardColor(Color.rgb(255,255,255), n-1,0,1,m);
        cardEdgeR.checkCollision();
        cardEdgeT = gameLib.addCardColor(Color.rgb(255,255,140), 1,0,n-2,1);
        cardEdgeT.checkCollision();
        cardEdgeB = gameLib.addCardColor(Color.rgb(255,255,140), 1,m-1,n-2,1);
        cardEdgeB.checkCollision();
        cardRacket1 = gameLib.addCardColor(Color.rgb(255,255,255), 8,2,4,1);
        cardRacket1.checkCollision();
        cardRacket2 = gameLib.addCardColor(Color.rgb(255,255,255), 8,m-3,4,1);
        cardRacket2.checkCollision();
        cardScore1 = gameLib.addCardColor(Color.argb(0,0,0,0), 1,13,4,4);
        cardScore1.text("0", Color.rgb(128,128,128), 100);
        cardScore2 = gameLib.addCardColor(Color.argb(0,0,0,0), 1,17,4,4);
        cardScore2.text("0", Color.rgb(128,128,128), 100);
        cardBall = gameLib.addCardColor(Color.rgb(255,255,255), 1,10,1,1);
        cardBall.checkCollision();
    }

    void newBall() {
        cardScore1.text("" + scoreCom);
        cardScore2.text("" + scoreMe);
        int x = gameLib.random(1, n-2);
        cardBall.move(x,10);
        double speed = 0.4;
        if(gameLib.random(0,1) == 0)
            cardBall.movingEndless(speed, speed);
        else
            cardBall.movingEndless(-speed, speed);
    }

    void stopGame() {
        cardRacket1.stopMoving();
        cardBall.stopMoving();
        gameLib.popupDialog(null, "Game is finished!", "Close");
    }

    // User Event start ====================================

    public void onStart(View v) {
        scoreMe = 0; scoreCom = 0;
        cardRacket1.move(8, 2);
        cardRacket1.moving(15,2, 1.0);
        newBall();
    }

    public void onBtnArrow(View v) {
        double gapHrz = 1;
        switch (v.getId()) {
            case R.id.btnLeft: {
                if(cardRacket2.screenRect().left < 1+gapHrz)
                    cardRacket2.move(1, m-3);
                else
                    cardRacket2.moveGap(-gapHrz, 0);
                break;
            }
            default: {
                if(cardRacket2.screenRect().left > 15-gapHrz)
                    cardRacket2.move(15, m-3);
                else
                    cardRacket2.moveGap(gapHrz, 0);
            }
        }
    }

    // User Event end ====================================

    // Game Event start ====================================

    public void onGameWorkEnded(JGameLib.Card card, JGameLib.WorkType workType) {
        if(card == cardRacket1) {
            if(cardRacket1.screenRect().left > 2)
                cardRacket1.moving(1,2, 1.0);
            else
                cardRacket1.moving(15,2, 1.0);
        }
    }

    public void onGameTouchEvent(JGameLib.Card card, int action, float blockX, float blockY) {}

    public void onGameSensor(int sensorType, float x, float y, float z) {}

    public void onGameCollision(JGameLib.Card card1, JGameLib.Card card2) {
        if (cardEdgeL.equals(card1) || cardEdgeR.equals(card1)) {
            if(cardEdgeL.equals(card1))
                cardBall.move(1, cardBall.screenRect().top);
            else
                cardBall.move(n-2, cardBall.screenRect().top);
            cardBall.movingEndless(-cardBall.unitHrz, cardBall.unitVtc);
        } else if (cardRacket1.equals(card1) || cardRacket2.equals(card1)) {
            cardBall.movingEndless(cardBall.unitHrz, -cardBall.unitVtc);
        } else if(cardEdgeT.equals(card1) || cardEdgeB.equals(card1)) {
            if(cardEdgeT.equals(card1))
                scoreMe ++;
            else
                scoreCom ++;
            newBall();
            if(scoreMe >= 10 || scoreCom >= 10)
                stopGame();
            //else
        }
    }

    // Game Event end ====================================

}