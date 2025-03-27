package jprime.multithread;

public class BlockOperationsModel {
    private static final int NB_LONG = 90;  //5760 possibles primes numbers store into 5760 bits ==> 90 longs
    private final BlockOperationsByIndex[] tabBlockLong = new BlockOperationsByIndex[NB_LONG];

    public BlockOperationsModel() {
        for(int indexLong = 0; indexLong < NB_LONG; indexLong++) {
            tabBlockLong[indexLong] = new BlockOperationsByIndex(indexLong);
        }
    }

    public BlockOperationsByIndex getForIndex(int index) {
        return tabBlockLong[index];
    }
}
