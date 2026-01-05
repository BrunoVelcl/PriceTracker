package DataFetcher.repositories.implementations;

import DataFetcher.entities.Chain;
import DataFetcher.entities.ChainWebInfo;

//Hardcoded repository
public abstract class ChainWebInfoRepoImpl {
    public static final ChainWebInfo[] chainURLs;

    static  {
        chainURLs = new ChainWebInfo[Chain.values().length];

        chainURLs[Chain.LIDL.getIndex()] = new ChainWebInfo(
                Chain.LIDL,
                "https://www.lidl.hr/",
                "https://tvrtka.lidl.hr/cijene");

        chainURLs[Chain.KAUFLAND.getIndex()] = new ChainWebInfo(
                Chain.KAUFLAND,
                "https://www.kaufland.hr",
                "https://www.kaufland.hr/akcije-novosti/popis-mpc.html");

        chainURLs[Chain.SPAR.getIndex()] = new ChainWebInfo(
                Chain.SPAR,
                "https://www.spar.hr/",
                "https://www.spar.hr/usluge/cjenici");

        chainURLs[Chain.STUDENAC.getIndex()] = new ChainWebInfo(
                Chain.STUDENAC,
                "https://www.studenac.hr/",
                "https://www.studenac.hr/popis-maloprodajnih-cijena");

        chainURLs[Chain.PLODINE.getIndex()] = new ChainWebInfo(
                Chain.PLODINE,
                "https://www.plodine.hr/",
                "https://www.plodine.hr/info-o-cijenama");
    }

}
