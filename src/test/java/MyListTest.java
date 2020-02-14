import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * java类简单作用描述
 *
 * @ProjectName: InfectStatistic-main
 * @Description: java类作用描述
 * @Author: horse
 * @CreateDate: 2020/2/14 11:44
 * @UpdateUser: horse
 * @UpdateDate: 2020/2/14 11:44
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 */
public class MyListTest {

    @Test
    public void log() {
        MyList myList = new MyList(new CmdArgs("list -log D:\\log\\ -out D:\\ListOut1.txt " +
                "-date 2020-01-23 -type ip sp -province 全国 福建"));
        myList.log();
        assertTrue(myList.getDir().equals("D:\\log\\"));
    }

    @Test
    public void date() {
        MyList myList = new MyList(new CmdArgs("list -log D:\\log\\ -out D:\\ListOut1.txt " +
                "-date 2020-01-23 -type ip sp -province 全国 福建"));
        try {
            myList.log();
            myList.date();
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add("2020-01-22.log.txt");
            arrayList.add("2020-01-23.log.txt");
            assertTrue(equalsArrayList(arrayList, myList.getLogPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //以下两个方法，无法解耦，结合 -out 一起测试
/*    @Test
    public void province() {
    }

    @Test
    public void type() {
    }*/

    @Test
    public void out() {
        try {
            String[] lists=FileOperate.readFile("./src/test/resource/list/list.txt").split("#");
            String[] listOuts=FileOperate.readFile("./src/test/resource/list/listOut.txt").split("#");

            for(int i=0;i<lists.length;i++){
                MyList myList=new MyList(new CmdArgs(lists[i]));
                myList.log();
                myList.date();
                myList.province();
                myList.type();
                myList.out();

                String outStr=FileOperate.readFile(myList.getOutPath());
                String rightStr=FileOperate.readFile(listOuts[i]);

                assertTrue(outStr.equals(rightStr));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean equalsArrayList(ArrayList<String> listA, ArrayList<String> listB) {
        if (listA == null || listB == null) {
            return false;
        }
        if (listA.size() != listB.size()) {
            return false;
        }
        int flag = 0;
        for (int i = 0; i < listA.size(); i++) {
            for (int j = 0; j < listB.size(); j++) {
                if (listA.get(i).equals(listB.get(j))) {
                    flag++;
                }
            }
        }
        return flag == listA.size();
    }
}