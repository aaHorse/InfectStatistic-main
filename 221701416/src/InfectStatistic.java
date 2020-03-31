import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.FileOutputStream;  
import java.io.PrintStream;
/**
 * InfectStatistic
 *
 * @author Hanani
 * @version v1.0.0
 */

class InfectStatistic {
	
	public static String Log=new String();
	public static String Out=new String();
	static SimpleDateFormat Df = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ
	static String TString=Df.format(new Date());
	public static String Date=new String(TString);
	public static String []Type= new String[50];
	public static String []Province=new String[50];
	
    public static void main(String[] args) throws IOException {
    	CoronavirusDetail CD=new CoronavirusDetail();
    	CD.Init();
    	AnalysisCommand(args);
    	CD.ReadAll(Log,Out,Date,Type,Province);
    	CD.PrintDetail(Log,Out,Date,Type,Province);
    }
    
    static String $list= new String("list");
	static String $log= new String("-log");
	static String $out= new String("-out");
	static String $date= new String("-date");
	static String $type= new String("-type");
	static String $province= new String("-province");
	static String $nothing=new String("nothing");
	
    //������ƥ��
    public static boolean Match(String Th,String Tar) {
    	if(Tar.equals(Th)) 
    		return true;
    	return false;
    }
    
    //˫����ƥ��
    public static String MatchTot(String []Th,String Tar,int Len,String Ori) {
    	for(int i=1;i<Len;i++) {
    		if(Th[i].equals(Tar)) {
    			return Th[i+1];
    		}
    	}
    	return Ori;
    }
    
    //������ƥ��
    public static String[] MatchMuch(String []Th,String Tar,int Len,String[] Ori) {
    	
    	String []Now=new String[50];
    	
    	for(int i=0;i<50;i++) Now[i]=$nothing;
    	int Pos=0;
		for(int i=1;i<Len;i++) {
			if(Th[i].equals(Tar)) {
				Pos=i;
				break;
			}
		}
		
		int index=0;
		
		for(int i=Pos+1;i<Len;i++) {
			if(Th[i].charAt(0)!='-') {
				Now[index++]=Th[i];
			}else break;
		}
		
		return Now;
    }
    
    //�������������д���ֽ�
    public static void AnalysisCommand(String[] Command) {
    	
    	int Len=Command.length;
    	
    	//��ʼ��type��province
    	for(int i=0;i<50;i++) Type[i]=$nothing;
    	for(int i=0;i<50;i++) Province[i]=$nothing;
    	
    	//list���
		if(!(Match(Command[0],$list))) {
			System.out.println("��ʹ��list������в���");
			System.exit(0);
		}
		
		//��ȡlogĿ¼,outĿ¼,date��ֵ
		Log=MatchTot(Command,$log,Len,Log);
		Out=MatchTot(Command,$out,Len,Out);
		Date=MatchTot(Command,$date,Len,Date);
		//��ȡtype�Ĳ�����province�Ĳ���
		Type=MatchMuch(Command,$type,Len,Type);
		Province=MatchMuch(Command,$province,Len,Province);
		
    }
}



/**
 * CoronavirusDetail
 *
 * @author Hanani
 * @version v1.0.0
 */
class CoronavirusDetail{
	
	String $nothing=new String("nothing");
	Map ProvinceMap= new HashMap();	
	Map TypeMap=new HashMap();
	String[] ProvinceStr= {
			"ȫ��"  ,"����"  ,"����"  ,"����"  ,"����"  ,"����"  ,
			"�㶫"  ,"����"  ,"����"  ,"����"  ,"�ӱ�"  ,
			"����"  ,"������","����"  ,"����"  ,"����"  ,
			"����"  ,"����"  ,"����"  ,"���ɹ�","����"  ,
			"�ຣ"  ,"ɽ��"  ,"ɽ��"  ,"����"  ,"�Ϻ�"  ,
			"�Ĵ�"  ,"���"  ,"����"  ,"�½�"  ,"����"  ,"�㽭" 
	};
	String[] TypeStr={
		"ip" , "sp", "cure", "dead"
	};
	String[] TypeStrCn={
			"��Ⱦ����" , "���ƻ���", "����", "����"
		};
	int _provincenum=32;
	int _detailnum=4;
	public int [][] Detail=new int[_provincenum][_detailnum];
	
	//��ʼ��ʡ����Ϣ
	public void Init() {
		//����ʡ����Ϣ31��
		//��ʡ��ע��Map�з������ʹ��
		for(int i=0;i<ProvinceStr.length;i++) {
			ProvinceMap.put(ProvinceStr[i], Integer.valueOf(i));
		}
		
		for(int i=0;i<TypeStr.length;i++) {
			TypeMap.put(TypeStr[i],Integer.valueOf(i));
		}
		
		for(int i=0;i<_provincenum;i++)
			for(int j=0;j<_detailnum;j++) Detail[i][j]=0;
	}
	
	//���������Ϣ
    public void PrintDetail(String Log,String Out,String Date,
    		String [] Type,String [] Province) throws IOException {
    	
    	File file=new File(Out);
    	
    	if(!file.exists()) {
    		file.createNewFile();
    	}
    	
    	PrintStream Ps=new PrintStream(Out);
    	System.setOut(Ps);
    	
    	for(int i=1;i<32;i++) 
    		for(int j=0;j<4;j++) Detail[0][j]+=Detail[i][j];
    	for(int i=0;i<ProvinceStr.length;i++) {
        	for(int j=0;j<Province.length;j++) 
        	if(ProvinceStr[i].equals(Province[j])){
        		System.out.print(ProvinceStr[i]+" ");
        		Integer Pronum=(Integer) ProvinceMap.get(ProvinceStr[i]);
        		int Typecnt=0;
        		for(int k=0;k<4;k++) {
        			if(Type[k].equals($nothing)) Typecnt++;
        		}
        		if(Typecnt==4)
        			for(int k=0;k<4;k++)
        				Type[k]=TypeStr[k];
        		for(int k=0;k<4;k++) 
        		if(!Type[k].equals($nothing)){
        			Integer Tynum=(Integer) TypeMap.get(Type[k]);
        			System.out.print(TypeStrCn[Tynum]+Detail[Pronum][Tynum]+" ");
        		}
        		System.out.println();
        	}
    	}
    	
    }
    
	//��ȡָ������֮ǰ���ļ���
	public static ArrayList<String> GetFilesName(String Path,String Date){
		ArrayList<String> Files = new ArrayList<String>();
		ArrayList<String> Beforefiles = new ArrayList<String>();
	    File File = new File(Path);
	    File[] TempList = File.listFiles();
	    
	    for (int i = 0; i < TempList.length; i++) {
	        if (TempList[i].isFile()) {
	            Files.add(TempList[i].toString());
	        }
	        //�õ���ǰ����֮ǰ���ļ���
	        File TempFile=new File(Files.get(i).trim());
	        String FileName=TempFile.getName().substring(0,10);
	        if(Date.compareTo(FileName)>=0){
	        	Beforefiles.add(Files.get(i));
	        }
	    }
	    return Beforefiles;
	}
	
	//��ȡ����ָ������֮ǰ���ļ�
	public void ReadAll(String Log,String Out,String Date,
			String [] Type,String [] Province) throws IOException{
		
		//�趨������ʽ����
		String MatString_1="(\\S+) ���� ��Ⱦ���� (\\d+)��";
		String SplitString_1=" ���� ��Ⱦ���� |��";
		
		String MatString_2="(\\S+) ���� ���ƻ��� (\\d+)��";
		String SplitString_2=" ���� ���ƻ��� |��";
		
		String MatString_3="(\\S+) ��Ⱦ���� ���� (\\S+) (\\d+)��";
		String SplitString_3=" ��Ⱦ���� ���� | |��";
		
		String MatString_4="(\\S+) ���ƻ��� ���� (\\S+) (\\d+)��";
		String SplitString_4=" ���ƻ��� ���� | |��";
		
		String MatString_5="(\\S+) ���� (\\d+)��";
		String SplitString_5=" ���� |��";
		
		String MatString_6="(\\S+) ���� (\\d+)��";
		String SplitString_6=" ���� |��";
		
		String MatString_7="(\\S+) ���ƻ��� ȷ���Ⱦ (\\d+)��";
		String SplitString_7=" ���ƻ��� ȷ���Ⱦ |��";
		
		String MatString_8="(\\S+) �ų� ���ƻ��� (\\d+)��";
		String SplitString_8=" �ų� ���ƻ��� |��";
		
		//�õ���Ҫ���ļ�·�����������
		ArrayList<String> TeArrayList=GetFilesName(Log,Date);
		for(int i=0;i<TeArrayList.size();i++) {
			//System.out.println(teArrayList.get(i));

			BufferedReader InBufferedReader=
					new BufferedReader(new InputStreamReader(
							new FileInputStream(TeArrayList.get(i)), "UTF-8"));
			String NowString;
			while((NowString=InBufferedReader.readLine())!=null) {
				//System.out.print(nowString);
				//System.out.println("???");
				//<ʡ> ���� ��Ⱦ���� n��
				if(NowString.matches(MatString_1)) {
					String [] Strings_1=NowString.split(SplitString_1);
					Detail[(Integer) ProvinceMap.get(Strings_1[0])][0]+=
							Integer.parseInt(Strings_1[1]);
				}
				//<ʡ> ���� ���ƻ��� n��
				else if(NowString.matches(MatString_2)) {
					String [] Strings_2=NowString.split(SplitString_2);
					Detail[(Integer) ProvinceMap.get(Strings_2[0])][1]+=
							Integer.parseInt(Strings_2[1]);
				}
				//<ʡ1> ��Ⱦ���� ���� <ʡ2> n��
				else if(NowString.matches(MatString_3)) {
					String [] Strings_3=NowString.split(SplitString_3);
					Detail[(Integer) ProvinceMap.get(Strings_3[0])][0]-=
							Integer.parseInt(Strings_3[2]);
					Detail[(Integer) ProvinceMap.get(Strings_3[1])][0]+=
							Integer.parseInt(Strings_3[2]);
				}
				//<ʡ1> ���ƻ��� ���� <ʡ2> n��
				else if(NowString.matches(MatString_4)) {
					String [] Strings_4=NowString.split(SplitString_4);
					Detail[(Integer) ProvinceMap.get(Strings_4[0])][1]-=
							Integer.parseInt(Strings_4[2]);
					Detail[(Integer) ProvinceMap.get(Strings_4[1])][1]+=
							Integer.parseInt(Strings_4[2]);
				}
				//<ʡ> ���� n��
				else if(NowString.matches(MatString_5)) {
					String [] Strings_5=NowString.split(SplitString_5);
					Detail[(Integer) ProvinceMap.get(Strings_5[0])][3]+=
							Integer.parseInt(Strings_5[1]);
					Detail[(Integer) ProvinceMap.get(Strings_5[0])][0]-=
							Integer.parseInt(Strings_5[1]);
				}
				//<ʡ> ���� n��
				else if(NowString.matches(MatString_6)) {
					String [] Strings_6=NowString.split(SplitString_6);
					Detail[(Integer) ProvinceMap.get(Strings_6[0])][2]+=
							Integer.parseInt(Strings_6[1]);
					Detail[(Integer) ProvinceMap.get(Strings_6[0])][0]-=
							Integer.parseInt(Strings_6[1]);
				}
				//<ʡ> ���ƻ��� ȷ���Ⱦ n��
				else if(NowString.matches(MatString_7)) {
					String [] Strings_7=NowString.split(SplitString_7);
					Detail[(Integer) ProvinceMap.get(Strings_7[0])][0]+=
							Integer.parseInt(Strings_7[1]);
					Detail[(Integer) ProvinceMap.get(Strings_7[0])][1]-=
							Integer.parseInt(Strings_7[1]);
				}
				//<ʡ> �ų� ���ƻ��� n��
				else if(NowString.matches(MatString_8)) {
					String [] Strings_8=NowString.split(SplitString_8);
					Detail[(Integer) ProvinceMap.get(Strings_8[0])][1]-=
							Integer.parseInt(Strings_8[1]);
				}
				//System.out.println("+++++");
			}
		}
		
	}
}