package learn.learn;

import java.io.IOException;

public class Packed{
	  static class Packed64SingleBlock4{
		  	private long[] blocks;
		    Packed64SingleBlock4(int valueCount) {
		    	final int valuesPerBlock = 64 / 4;
		    	this.blocks = new long[requiredCapacity(valueCount,valuesPerBlock)];
		    }
		    
		    private static int requiredCapacity(int valueCount, int valuesPerBlock) {
		        return valueCount / valuesPerBlock
		            + (valueCount % valuesPerBlock == 0 ? 0 : 1);
		    }
		    
		    public long get(int index) {
		      final int o = index >>> 4;
		      final int b = index & 15;
		      final int shift = b << 2;
		      return (blocks[o] >>> shift) & 15L;
		    }

		    public void set(int index, long value) {
		      final int o = index >>> 4;
		      final int b = index & 15;
		      final int shift = b << 2;
		      blocks[o] = (blocks[o] & ~(15L << shift)) | (value << shift);
		    }

	}
	/*
	 * 假设我们有如下的16个整数
	 * array = [0, 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]
	 * 如果使用short int 存储的话, 一共需要16*16=256 bit
	 * 而使用Packed64SingleBlock4则只需要 64 bit
	 * 
	 * 在实际业务中,  这种类型的数据比较常见. 比如中国一共就30多个省. 普通高校招生一共只有11个学科门类.
	 * 成人的身高一般集中在160cm左右.....
	 * 
	 * 所以个人的理解, Lucene的Packed压缩是考虑到了业务中的数据分布特点 
	 * */
  public static void main( String[] args ) throws IOException, InterruptedException
  {	
  	int valueCount = 16;
  	Packed64SingleBlock4 block = new Packed64SingleBlock4(valueCount);
  	for(int i=0;i<valueCount;i++)
  		block.set(i, i);
  	for(int i=0;i<valueCount;i++)
  		System.out.println(i+": "+block.get(i));
  }
}
