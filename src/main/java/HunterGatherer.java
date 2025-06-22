//executes all steps necessary for preparing data for having up-to-date store provided .csv (hunts for and gathers files :^)

import java.util.List;

public class HunterGatherer {
    public void run(){
        LinkFetcher linkFetcher= new LinkFetcher();
        Downloader downloader = new Downloader();
        fetchAll(linkFetcher);
        downloader.download();
    }

    private void fetchAll(LinkFetcher hunter){
        hunter.getLinksLidl();
        hunter.getLinksPlodine();
        hunter.getLinksStudenac();
        hunter.getLinksKaufland();
        hunter.getLinksSpar();
        hunter.writeLinksToDisk();
        hunter.free();
    }
}
