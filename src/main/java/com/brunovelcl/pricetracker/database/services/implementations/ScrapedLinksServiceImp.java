package com.brunovelcl.pricetracker.database.services.implementations;

import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.entities.ScrapedLink;
import com.brunovelcl.pricetracker.database.repositories.ScrapedLinksRepository;
import com.brunovelcl.pricetracker.database.services.interfaces.ScrapedLinksService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ScrapedLinksServiceImp implements ScrapedLinksService {

    private final ScrapedLinksRepository scrapedLinksRepository;

    public ScrapedLinksServiceImp(ScrapedLinksRepository scrapedLinksRepository) {
        this.scrapedLinksRepository = scrapedLinksRepository;
    }

    public void addNew(ScrapedLink scrapedLink) {
        try {
            //custom query, after this point use only persisted ScrapedLinks
            scrapedLinksRepository.insertIgnoreDuplicate(scrapedLink.getLink(), scrapedLink.getFilename(), scrapedLink.getChain().getId());
        } catch (Exception e) {
            System.err.printf(Text.ErrorMessages.SCRAPED_LINK_PERSIST_FAILED, e);
        }
    }

    public List<ScrapedLink> findByProcessedFalse(Integer chainId){
        return scrapedLinksRepository.findByProcessedFalseAndChainId(chainId);
    };

    public boolean processedSuccessfully(List<ScrapedLink> scrapedLinks, List<Long> failedIds) {
        if (scrapedLinks.isEmpty()) return true;
        List<Long> setToTrueList = new ArrayList<>();
        if (!failedIds.isEmpty()) {
            Collections.sort(failedIds);
            scrapedLinks.forEach(scrapedLink -> {
                if (Collections.binarySearch(failedIds, scrapedLink.getId()) < 0) {
                    setToTrueList.add(scrapedLink.getId());
                }
            });
            if (setToTrueList.isEmpty()) return true;
        }else{
            scrapedLinks.forEach(scrapedLink -> {
                setToTrueList.add(scrapedLink.getId());
            });
        }

        return (scrapedLinksRepository.processedSuccessfully(setToTrueList) == 1);
    }
}
