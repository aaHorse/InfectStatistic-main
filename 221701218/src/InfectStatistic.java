import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * InfectStatistic
 * TODO
 *
 * @author Spike
 * @version 1.0
 * @since 2020-2-12
 */
class InfectStatistic{
    public static void main(String[] args) { 
        if(args.length!=0) {    //无命令行参数出错，不能进行处理
        	CmdAnalysis cmdObject = new CmdAnalysis(args);
            if(cmdObject.isCmdString()) {
                //cmdObject.showAll();
                HandleLog handleObject = new HandleLog(cmdObject.getLogLocation(),cmdObject.getOutLocation(),cmdObject.getLogDate(),
                		cmdObject.getTypeOrder(),cmdObject.getProvinceShow());
                handleObject.readLog();
                handleObject.writeLog();
                //handleObject.showAll();  
            }
        }
    }
    
}
/**
  *解析命令行参数
 */
class CmdAnalysis{
	
	private String[] cmdString;    //命令行参数
	private String logLocation;    //日志文件位置
	private String outLocation;    //输出位置
	private String logDate;    //输出至此日期
	private int[] typeOrder = {0,1,2,3};	//记录输出类型的下标顺序，默认0123ip、sp、cure、dead,-1不必输出
	static String[] typeString = {"ip","sp","cure","dead"};    //四种类型
	private int[] provinceShow = new int[33];	//0~31表示若带-province参数的要求输出省份，-1不必输出，0要求输出，而32标记是否含有-province参数-1不包含，0包含
	static String[] province = {"全国", "安徽","北京", "重庆","福建","甘肃",    //各省份拼音排序，便于对比
			"广东", "广西", "贵州", "海南", "河北", "河南", "黑龙江", "湖北", "湖南", "吉林",
			"江苏", "江西", "辽宁", "内蒙古", "宁夏", "青海", "山东", "山西", "陕西", "上海",
			"四川", "天津", "西藏", "新疆", "云南", "浙江"};    
	
	public CmdAnalysis(String []args) {    //构造函数并含相关变量的初始化
	    cmdString = args;
	    //设置默认指定日期为一个接近无穷大的日期，这样方便比较，设置默认为全部统计
	    logDate = "9999-12-31";
	    for(int i = 0;i < provinceShow.length;i++)
	    	provinceShow[i] = -1;    //起始全默认为-1，不必输出，且初始不含参数-province
	}
	/*
         *判断命令行参数是否正确，若正确则赋值保存
     *@return boolean 符合要求的命令行参数 true 否则 false
	 */
	public boolean isCmdString() {
		
		boolean mustLog = false;    //标记两个必选项-log -out 是否有输入
		boolean mustOut = false;
		if(!cmdString[0].equals("list")) {
			System.out.println("命令行参数缺少list");
			return false;
		}
		for(int i = 0;i < cmdString.length;i++) {
			if(cmdString[i].equals("-log")) {
				mustLog = true;
				logLocation = cmdString[++i];
			}
			else if(cmdString[i].equals("-out")) {
				mustOut = true;
				outLocation = cmdString[++i];
			}
			else if(cmdString[i].equals("-date")) {
				//检测日期合法性
				if(!isCorrectDate(++i)) {
					System.out.println("指定日期不合法");
					return false;
				}
			}
			else if(cmdString[i].equals("-type")) {
				//检测输入类型合法性（ip\sp\cure\dead)
				if(!isType(++i)) {
					System.out.println("指定类型不合法");
					return false;
				}
			}
			else if(cmdString[i].equals("-province")) {
				provinceShow[32] = 0;    //标识含-province参数，应依据输入输出，否则含默认方式（日志中涉及省份）展示
				if(!isProvince(++i)) {
					System.out.println("指定省份不合法");
					return false;
				}
			}
		}
		if(mustLog && mustOut)    //验证必选项是否输入
			return true;
		else {
			System.out.println("缺少输入-log 或 -out");
			return false;
		}
			
	}
	/*
	  *判断指定日期是否正确
	 *@param 命令行位置（下标）
	 *@return boolean 日期符合格式 true 否则 false
	 */
	private boolean isCorrectDate(int i) {
		
		if(i<cmdString.length) {
			if(isValidDate(cmdString[i])) {
				logDate = cmdString[i];
				return true;
			}else 
				return false;
		}else
			return false;
	}
	/*
	  *判断指定日期格式是否满足yyyy-MM-dd 字符串是否为数字
	 *@param String
	 *@return boole if String 满足格式 true 不满足false
	 */
	private boolean isValidDate(String strDate) {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
            format.setLenient(false);
            Date date = format.parse(strDate);    //利用创建日期来判断格式，若抛出异常则日期格式有误
            String[] sArray = strDate.split("-");
            for (String s : sArray) {
                boolean isNum = s.matches("[0-9]+");
                if (!isNum) {
                    return false;
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        return true;
	}
	/*
	  *判断指定类型是否正确，正确则记录输出类型的种类及顺序
	 *@param int 在命令行的位置
	 *@return boolean 满足类型true 不满足false
	 */
	private boolean isType(int i) {

		for(int a = 0;a < typeOrder.length;a++)
			typeOrder[a] = -1;    //-1不输出
		int currentIndex = i;
		if(i<cmdString.length) {
			int t = 0;
			for(;currentIndex < cmdString.length; currentIndex ++) {
				if(cmdString[currentIndex].equals(typeString[0])) {
					if(t < typeOrder.length)
						typeOrder[t] = 0;    
					t++;
				}	
				else if(cmdString[currentIndex].equals(typeString[1])) {
					if(t < typeOrder.length)
						typeOrder[t] = 1;
					t++;
				}
				else if(cmdString[currentIndex].equals(typeString[2])) {
					if(t < typeOrder.length)
						typeOrder[t] = 2;
					t++;
				}
				else if(cmdString[currentIndex].equals(typeString[3])) {
					if(t < typeOrder.length)
						typeOrder[t] = 3;
					t++;
				}
				else {
					break;
				}
			}
			if(t > 0 )
				return true;
			else
				return false;
		}else
			return false;
	}
	/*
	  * 判断指定省份是否正确
	 * @param int 在命令行的位置
	 * @return boolean 满足省份格式 true 不满足false
	 */
	private boolean isProvince(int i) {
		int currentIndex = i;
		if(i<cmdString.length) {
			for(;currentIndex < cmdString.length  && provinceShow[32] == 0; currentIndex ++) {
				for(int j = 0;j < province.length;j++) {
					if(cmdString[currentIndex].equals(province[j]))
						provinceShow[j] = 0;    //要求输出的省份
				}
			}
			return true;
		}else
			return false;
	}
	/*
	  * 获得私有变量的值（类似属性）便于后续处理和按要求输出
	 */
	public String getLogLocation() {
		return logLocation;
	}
	public String getOutLocation() {
		return outLocation;
	}
	public String getLogDate() {
		return logDate;
	}
	public int[] getTypeOrder() {
		return typeOrder;
	}
	public int[] getProvinceShow() {
		return provinceShow;
	}
	
}

/**
 *读取并统计日志文件
 */
class HandleLog{
	private String logLocation;   //日志位置
	private String outLocation;    //输出位置
	private String logDate;    //指定日期
	private int[] typeOrder;    //类型输出顺序
	private int[] provinceShow = new int[33];    //应该展示的省份值为0不展示为-1，32为标识是否含命令参数-province，以便分类处理省份输出
	//统计各省 各类型患者 数目
	private int[][] sum = new int[32][4];    //32行表示全国~浙江 4列表示ip,sp,cure,dead	初始为0
	static String[] typeString = {"感染患者","疑似患者","治愈","死亡"};    //输出类型的名称
	/*
	 * 构造函数，利用相关参数才能进行相应处理
	 */
	public HandleLog(String logLocation,String outLocation,String logDate,int[] typeOrder,int[] provinceShow) {
		this.logLocation = logLocation;
		this.outLocation = outLocation;
		this.logDate = logDate;
		this.typeOrder = (int[])typeOrder.clone();
		this.provinceShow = (int[])provinceShow.clone();
		if(this.provinceShow[32] == -1) {
			this.provinceShow[0] = 0;
		}
		
		for(int i = 0;i < sum.length;i++)
			Arrays.fill(sum[i], 0);    //各省各类型起始数目起始化为0 
	}
	/*
	 * 进入log目录读取日志文件
	 */
	public void readLog() {
		File file = new File(logLocation); 
		File[] files = file.listFiles();
		for(int i = 0;i < files.length;i++) {
			if(file.isDirectory()) {
				String filePath = files[i].getPath();
				String fileName = files[i].getName();
				//System.out.println(fileName);
				if(fileName.compareTo(logDate + ".log.txt") <= 0) {
					//System.out.println(filePath);
					statistics(filePath);
				}
			}
		}
	}
	/*
	 * 处理符合条件的日志文件并输出
	 */
	public void writeLog() {
		BufferedWriter fw = null;
		try {
			File file = new File(outLocation);	
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8")); // 指定编码格式，以免写时中文字符异常
			for(int i = 0;i < provinceShow.length-1;i++) {
				if(provinceShow[i] != -1) {
					fw.append(CmdAnalysis.province[i]+" ");
					for(int j = 0;j<typeOrder.length;j++) {
						if(typeOrder[j] != -1) {
							fw.append(typeString[typeOrder[j]]+""+sum[i][typeOrder[j]]+"人 ");
						}
					}
					fw.append("\n");
				}
			}
			fw.append("// 该文档并非真实数据，仅供测试使用");
			fw.flush(); // 全部写入缓存中的内容
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * 分类别统计：0新增 感染，1新增 疑似，2感染 流入，3疑似 流入，4死亡，5治愈，6疑似确诊感染，7排除
	 * @param String 目录路径
	 */
	private void statistics(String filePath) {
		//[\\u4E00-\\u9FA5]+ 匹配多个中文字符
		String handleType[] = {"[\\u4E00-\\u9FA5]+ 新增 感染患者 \\d+人","[\\u4E00-\\u9FA5]+ 新增 疑似患者 \\d+人",
				"[\\u4E00-\\u9FA5]+ 感染患者 流入 [\\u4E00-\\u9FA5]+ \\d+人","[\\u4E00-\\u9FA5]+ 疑似患者 流入 [\\u4E00-\\u9FA5]+ \\d+人",
				"[\\u4E00-\\u9FA5]+ 死亡 \\d+人","[\\u4E00-\\u9FA5]+ 治愈 \\d+人","[\\u4E00-\\u9FA5]+ 疑似患者 确诊感染 \\d+人",
				"[\\u4E00-\\u9FA5]+ 排除 疑似患者 \\d+人"};
		BufferedReader reader = null;
		try {
			// 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8")); 
			String str = null;
			while ((str = reader.readLine()) != null) {
				// 注释部分 "//"不处理
				if(!str.startsWith("//")) {
					if(str.matches(handleType[0])) {
						increaseIp(str);
					}
					else if(str.matches(handleType[1])){
						increaseSp(str);
					}
					else if(str.matches(handleType[2])){
						ipTransfer(str);
					}
					else if(str.matches(handleType[3])){
						spTransfer(str);
					}
					else if(str.matches(handleType[4])){
						dead(str);
					}
					else if(str.matches(handleType[5])){
						cure(str);
					}
					else if(str.matches(handleType[6])){
						spDiagnose(str);
					}
					else if(str.matches(handleType[7])){
						spExclude(str);
					}
				}
				//System.out.println(str);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * 对日志文件逐行处理，新增感染，全国、感染省份 感染人数增加num人
	 * @param 日志中的每一行
	 */
	private void increaseIp(String str) {
		String[] strArray = str.split(" ");	//以空格将一行输入分为一组字符串，便于统计
		String numIp = strArray[3].substring(0, strArray[3].length()-1);    //感染人数
		int num = Integer.parseInt(numIp);    //转换为整数便于四则运算
		sum[0][0] += num;    //全国感染人数增加
		for(int i = 0;i < CmdAnalysis.province.length;i++) {
			if(strArray[0].equals(CmdAnalysis.province[i])) {   //具体到某个省感染人数增加
				sum[i][0] += num;
				if(provinceShow[32] == -1) {    //按默认方式，标记日志中出现的省份 以默认显示，否则则按指定省份显示
					provinceShow[i] = 0;
				}
			}
		}
	}
	/*
	 * 新增疑似 全国 疑似省份 疑似人数增加num人
	 * @param 日志中的每一行
	 */
	private void increaseSp(String str) {
		String[] strArray = str.split(" ");	//以空格将一行输入分为一组字符串，便于统计
		String numSp = strArray[3].substring(0, strArray[3].length()-1);    //疑似人数
		int num = Integer.parseInt(numSp);    //转换为整数便于四则运算
		sum[0][1] += num;    //全国感染人数增加
		for(int i = 0;i < CmdAnalysis.province.length;i++) {
			if(strArray[0].equals(CmdAnalysis.province[i])) {   //具体到某个省感染人数增加
				sum[i][1] += num;
				if(provinceShow[32] == -1) {    //按默认方式，标记日志中出现的省份 以默认显示，否则则按指定省份显示
					provinceShow[i] = 0;
				}
			}
		}
	}
	/*
	 * 感染流入 全国不变 流出省感染人数减少num 流入省感染人数增加num
	 * @param 日志中的每一行
	 */
	private void ipTransfer(String str) {
		String[] strArray = str.split(" ");	//以空格将一行输入分为一组字符串，便于统计
		String numIp = strArray[4].substring(0, strArray[4].length()-1);    //感染流动人数
		int num = Integer.parseInt(numIp);    //转换为整数便于四则运算
		//全国感染人数不变
		for(int i = 0;i < CmdAnalysis.province.length;i++) {
			if(strArray[0].equals(CmdAnalysis.province[i])) {   //流出省感染人数减少
				sum[i][0] -= num;
				if(provinceShow[32] == -1) {    //按默认方式，标记日志中出现的省份 以默认显示，否则则按指定省份显示
					provinceShow[i] = 0;
				}
			}
			else if(strArray[3].equals(CmdAnalysis.province[i])) {   //流入感染人数增加
				sum[i][0] += num;
				if(provinceShow[32] == -1) {    //按默认方式，标记日志中出现的省份 以默认显示，否则则按指定省份显示
					provinceShow[i] = 0;
				}
			}
		}
	}
	/*
	 * 疑似流入 全国不变 流出省疑似人数减少num 流入省疑似人数增加num
	 * @param 日志中的每一行
	 */
	private void spTransfer(String str) {
		String[] strArray = str.split(" ");	//以空格将一行输入分为一组字符串，便于统计
		String numSp = strArray[4].substring(0, strArray[4].length()-1);    //疑似流动人数
		int num = Integer.parseInt(numSp);    //转换为整数便于四则运算
		//全国感染人数不变
		for(int i = 0;i < CmdAnalysis.province.length;i++) {
			if(strArray[0].equals(CmdAnalysis.province[i])) {   //流出省疑似人数减少
				sum[i][1] -= num;
				if(provinceShow[32] == -1) {    //按默认方式，标记日志中出现的省份 以默认显示，否则则按指定省份显示
					provinceShow[i] = 0;
				}
			}
			else if(strArray[3].equals(CmdAnalysis.province[i])) {   //流入疑似人数增加
				sum[i][1] += num;
				if(provinceShow[32] == -1) {    //按默认方式，标记日志中出现的省份 以默认显示，否则则按指定省份显示
					provinceShow[i] = 0;
				}
			}
		}
	}
	/*
	 * 死亡 全国 死亡省 死亡人数增加num 感染人数减少num
	 * @param 日志中的每一行
	 */
	private void dead(String str) {
		String[] strArray = str.split(" ");	//以空格将一行输入分为一组字符串，便于统计
		String numDead = strArray[2].substring(0, strArray[2].length()-1);    //死亡或者治愈人数
		int num = Integer.parseInt(numDead);    //转换为整数便于四则运算
		sum[0][0] -= num;    
		sum[0][3] += num;    //全国感染人数减少 死亡人数增加
		for(int i = 0;i < CmdAnalysis.province.length;i++) {
			if(strArray[0].equals(CmdAnalysis.province[i])) {   //具体到某个省感染人数减少
				sum[i][0] -= num;
				sum[i][3] += num;
				if(provinceShow[32] == -1) {    //按默认方式，标记日志中出现的省份 以默认显示，否则则按指定省份显示
					provinceShow[i] = 0;
				}
			}
		}
	}
	/*
	 * 治愈 全国、治愈省 治愈数目增加num 感染数目减少num
	 * @param 日志中的每一行
	 */
	private void cure(String str) {
		String[] strArray = str.split(" ");	//以空格将一行输入分为一组字符串，便于统计
		String numDead = strArray[2].substring(0, strArray[2].length()-1);    //死亡或者治愈人数
		int num = Integer.parseInt(numDead);    //转换为整数便于四则运算
		sum[0][0] -= num;    
		sum[0][2] += num;    //全国感染人数减少 治愈人数增加
		for(int i = 0;i < CmdAnalysis.province.length;i++) {
			if(strArray[0].equals(CmdAnalysis.province[i])) {   //具体到某个省感染人数减少
				sum[i][0] -= num;
				sum[i][2] += num; 
				if(provinceShow[32] == -1) {    //按默认方式，标记日志中出现的省份 以默认显示，否则则按指定省份显示
					provinceShow[i] = 0;
				}
			}
		}
	}
	/*
	 * 疑似确诊 全国、确诊省 感染人数增加num 疑似人数减少num
	 * @param 日志中的每一行
	 */
	private void spDiagnose(String str) {
		String[] strArray = str.split(" ");	//以空格将一行输入分为一组字符串，便于统计
		String numDiagnose = strArray[3].substring(0, strArray[3].length()-1);    //疑似确诊感染人数
		int num = Integer.parseInt(numDiagnose);    //转换为整数便于四则运算
		sum[0][0] += num; 
		sum[0][1] -= num;//全国疑似人数减少，感染人数增多
		for(int i = 0;i < CmdAnalysis.province.length;i++) {
			if(strArray[0].equals(CmdAnalysis.province[i])) {   //具体到某个省疑似人数减少，感染人数增多
				sum[i][0] += num;
				sum[i][1] -= num;
				if(provinceShow[32] == -1) {    //按默认方式，标记日志中出现的省份 以默认显示，否则则按指定省份显示
					provinceShow[i] = 0;
				}
			}
		}
	}
	/*
	 * 疑似排查 全国、排查省 疑似人数减少 num
	 * @param 日志中的每一行
	 */
	private void spExclude(String str) {
		String[] strArray = str.split(" ");	//以空格将一行输入分为一组字符串，便于统计
		String numDead = strArray[3].substring(0, strArray[3].length()-1);    //疑似排除人数
		int num = Integer.parseInt(numDead);    //转换为整数便于四则运算
		sum[0][1] -= num;    //全国疑似人数减少
		for(int i = 0;i < CmdAnalysis.province.length;i++) {
			if(strArray[0].equals(CmdAnalysis.province[i])) {   //具体到某个省疑似人数减少
				sum[i][1] -= num;
				if(provinceShow[32] == -1) {    //按默认方式，标记日志中出现的省份 以默认显示，否则则按指定省份显示
					provinceShow[i] = 0;
				}
			}
		}
	}
	
}