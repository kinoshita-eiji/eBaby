package application;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.tobeagile.training.ebaby.services.Auctionable;

public class BetterAuctionTimer {
    private Timer timer;
    private Auctionable auctions;

    public BetterAuctionTimer() {
    }

    public void checkAuction(Auctionable auctions) {
        this.auctions = auctions;
    }

    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() { public void run() {timerTick(); } }, 100, 100);
    }

    public void stop() {
        timer.cancel();
    }

    private void timerTick() {
        long now = (new Date()).getTime();
        auctions.handleAuctionEvents(now);
    }
}
