package application;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import com.tobeagile.training.ebaby.services.Auctionable;

import application.exception.InvalidAuctionTimeException;

public class Auctions implements Auctionable {

    private ArrayList<Auction> list = new ArrayList<>();

    @Override
    public void handleAuctionEvents(long now) {
        LocalDateTime ldtNow = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.systemDefault());
        for (Auction auction : getList()) {
            if (ldtNow.isAfter(auction.getStartTime())) {
                auction.setStatus(AuctionStatus.STARTED);
            }
            if (ldtNow.isAfter(auction.getEndTime())) {
                auction.setStatus(AuctionStatus.CLOSED);
            }
        }
    }

    public ArrayList<Auction> getList() {
        return list;
    }

    public void create(Auction auction) {

        if (!auction.getStartTime().isAfter(now())) {
            throw new InvalidAuctionTimeException("Start time must be in future");
        }
        if (!auction.getEndTime().isAfter(auction.getStartTime())) {
            throw new InvalidAuctionTimeException("end time must be greater than start time");
        }

        list.add(auction);
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public void setNow(LocalDateTime ldt) {
        throw new RuntimeException("This method is only for mocking.");
    }
}
