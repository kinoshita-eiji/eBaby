package application;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import com.tobeagile.training.ebaby.services.Auctionable;

public class Auctions implements Auctionable {

    ArrayList<Auction> list = new ArrayList<>();

    @Override
    public void handleAuctionEvents(long now) {
        LocalDateTime ldtNow = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.systemDefault());
        for (Auction auction : getList()) {
            if (ldtNow.isAfter(auction.startTime)) {
                auction.status = AuctionStatus.STARTED;
            }
            if (ldtNow.isAfter(auction.endTime)) {
                auction.status = AuctionStatus.CLOSED;
            }
        }
    }

    public ArrayList<Auction> getList() {
        return list;
    }

    public void create(Auction auction) {
        list.add(auction);
    }

}
