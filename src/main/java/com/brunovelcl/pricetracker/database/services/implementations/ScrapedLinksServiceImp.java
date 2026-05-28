package com.brunovelcl.pricetracker.database.services.implementations;

import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.entities.ScrapedLink;
import com.brunovelcl.pricetracker.database.repositories.ScraoedLinksRepository;
import com.brunovelcl.pricetracker.database.services.interfaces.ScrapedLinksService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScrapedLinksServiceImp implements ScrapedLinksService {

    private final ScraoedLinksRepository scraoedLinksRepository;

    public ScrapedLinksServiceImp(ScraoedLinksRepository scraoedLinksRepository) {
        this.scraoedLinksRepository = scraoedLinksRepository;
    }

    public void addNew(ScrapedLink scrapedLink) {
        try {
            //custom query, after this point use only persisted ScrapedLinks
            scraoedLinksRepository.insertIgnoreDuplicate(scrapedLink.getLink(), scrapedLink.getFilename());
        } catch (Exception e) {
            System.err.printf(Text.ErrorMessages.SCRAPED_LINK_PERSIST_FAILED, e);
        }
    }

    public List<ScrapedLink> findByProcessedFalse(){
        return scraoedLinksRepository.findByProcessedFalse();
    };

    public boolean processedSuccessfully(ScrapedLink scrapedLink){
        return (scraoedLinksRepository.processedSuccessfully(scrapedLink.getId()) == 1);
    }
}
