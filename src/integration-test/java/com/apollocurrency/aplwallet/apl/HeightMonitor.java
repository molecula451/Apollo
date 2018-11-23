/*
 * Copyright © 2018 Apollo Foundation
 */

package com.apollocurrency.aplwallet.apl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import dto.Block;
import org.slf4j.Logger;

public class HeightMonitor {
    private static final Logger LOG = getLogger(HeightMonitor.class);
    private static final class MaxBlockDiffCounter {
        private int period;
        private int value;
        private long lastResetTime = System.currentTimeMillis() / (1000 * 60);

        public MaxBlockDiffCounter(int period) {
            this.period = period;
        }

        public void update(int currentBlockDiff, Writer writer) throws IOException {
            value = Math.max(value, currentBlockDiff);
            writer
                    .append(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .append(" - MAX Blocks diff for last")
                    .append(String.valueOf(period))
                    .append("h is  ")
                    .append(String.valueOf(value))
                    .append("\n");
            long currentTime = System.currentTimeMillis() / 1000 / 60;
            if (currentTime - lastResetTime >= period * 60 ) {
                lastResetTime = currentTime;
                value = currentBlockDiff;
            }
        }
    }
    public static void main(String[] args) {
        NodeClient client = new NodeClient();
        List<String> peers = Arrays.asList(
                "51.15.235.41",
                "163.172.146.173",
                "51.15.69.39",
                "51.15.59.37",
                "51.15.114.68"
        );
        List<String> peersUrls = peers.stream().map(peer -> "http://" + peer + ":6876/apl").collect(Collectors.toList());

        List<MaxBlockDiffCounter> maxBlockDiffCounters = Arrays.asList(
                new MaxBlockDiffCounter(3),
                new MaxBlockDiffCounter(6),
                new MaxBlockDiffCounter(12),
                new MaxBlockDiffCounter(24),
                new MaxBlockDiffCounter(96));
        try (FileWriter writer = new FileWriter("heightMonitorLogs")) {
            while (true) {

                Map<String, List<Block>> peerBlocks = new HashMap<>();
                for (int i = 0; i < peersUrls.size(); i++) {
                    try {
                        List<Block> blocksList = client.getBlocksList(peersUrls.get(i), false, null);
                        peerBlocks.put(peers.get(i), blocksList);

                    }
                    catch (Throwable e) {
                        writer.append(e.getMessage()).append(" Cannot connect to peer: ").append(peers.get(i)).append("\n");
                        peerBlocks.put(peers.get(i), new ArrayList<>());
                    }
                }
                int currentMaxBlocksDiff = -1;
                for (int i = 0; i < peers.size(); i++) {
                    List<Block> targetBlocks = peerBlocks.get(peers.get(i));
                    for (int j = i + 1; j < peers.size(); j++) {
                        List<Block> blocksToCompare = peerBlocks.get(peers.get(j));
                        Block lastMutualBlock = findLastMutualBlock(blocksToCompare, targetBlocks);
                        if (lastMutualBlock == null) {
                            writer.append(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).append(" - ERROR! No mutual blocks between ").append(peers.get(i)).append(" and ").append(peers.get(j)).append(
                                    "\n");
                            continue;
                        }
                        int lastHeight = targetBlocks.get(0).getHeight();
                        int mutualBlockHeight = lastMutualBlock.getHeight();
                        int blocksDiff = lastHeight - mutualBlockHeight;
                        currentMaxBlocksDiff = Math.max(blocksDiff, currentMaxBlocksDiff);

                        writer.append(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).append(" - Blocks diff is ").append(String.valueOf(blocksDiff)).append(" between peers ").append(peers.get(i)).append(" and ").append(peers.get(j)).append("\n");
                        writer.flush();
                    }
                }
                for (MaxBlockDiffCounter maxBlockDiffCounter : maxBlockDiffCounters) {
                    maxBlockDiffCounter.update(currentMaxBlocksDiff, writer);
                }
                TimeUnit.SECONDS.sleep(30);

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Block findLastMutualBlock(List<Block> blocksToCompare, List<Block> targetBlocks) {
        for (int i = 0; i < blocksToCompare.size(); i++) {
            Block block = blocksToCompare.get(i);
            if (targetBlocks.contains(block)) {
                return block;
            }
        }
        return null;
    }

    private static void logMaxBlocksDiff(int diff, long startTime, int hours) {

    }
}
