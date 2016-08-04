package algorithms.ds.fst;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Util;

import algorithms.util.ResLoader;

/*
 * @author 帅广应
 * @email 810050504@qq.com
 * 用FST实现的词典
 * */
public class FSTDic {
	private static final String MAIN_DIC="/org/xh/ds/fst/main.dic";
	private FST<BytesRef> fst;
	 private final FST.BytesReader fstReader;
	private final FST.Arc<BytesRef> scratchArc;
	
	private FST<BytesRef> build(){
		BufferedReader br=null;
		ByteSequenceOutputs output=ByteSequenceOutputs.getSingleton() ;
		Builder<BytesRef> builder =new Builder<BytesRef>(FST.INPUT_TYPE.BYTE4,output);
		try {
			br=ResLoader.getReader(MAIN_DIC, false);
			if(br==null)throw new IllegalAccessException("dic does not exist");
			String line;
			CharsRef input;
			final IntsRef scratchIntsRef = new IntsRef();
			while((line=br.readLine())!=null){
				if("".equals(line.trim())||line.startsWith("#"))continue;
				input=new CharsRef(line);
				builder.add(Util.toUTF32(input, scratchIntsRef), output.getNoOutput());
			}
			return builder.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public FSTDic(){
		fst=build();
		if(fst==null)throw new NullPointerException("fst build failure!");
		
		scratchArc = new FST.Arc<BytesRef>();
		fstReader=fst.getBytesReader();
	}
	public boolean contains(String string) {
		//得到FST的初始边，从初始边开始匹配
		fst.getFirstArc(scratchArc);
		 final char[] buffer=string.toCharArray();
		 int bufUpto = 0;
		 int bufferLen=buffer.length;
		 try {
			 while(bufUpto<bufferLen){
		        final int codePoint = Character.codePointAt(buffer, bufUpto, bufferLen);
		        if(fst.findTargetArc(codePoint, scratchArc, scratchArc, fstReader)==null){
		        	return false;
		        }
		        bufUpto += Character.charCount(codePoint);
			}
			if(scratchArc.isFinal())return true; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
