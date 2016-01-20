import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

public class CSE535Assignment {
    public static void main(String[] args) throws IOException
    {
        
        
        Docindex.createdoc(args[0]);       //create index by docid
        Docindex2.createdoc(args[0]);      //create another index to avoid duplication by docid operations
        System.setOut(new PrintStream(new FileOutputStream(args[1])));  //write to output file
        System.out.println("FUNCTION: getTopK "+args[2]);
        Docindex.gettopk(args[2]);     //get topk using docindex
        Termindex.createfreq(args[0]);  //create index with postings list ordered according to decreasing freq
        Querymanip.tok(args[3]); //pass the file with query string for manipulation
        
    }
}

class Querymanip {
    public static BufferedReader br = null;
    public static String strLine = "";
    public static int i;
    public static void tok (String s){
        try {
            br = new BufferedReader( new FileReader(s));
            while( (strLine = br.readLine()) != null) {
                String[] result=getlinetoken(strLine); // take one query and split it into its terms store each term in result array
                int qno=result.length;
                //System.out.println("query terms are!");
                for(i=0;i<qno;i++)
                {
                    
                    System.out.println("FUNCTION: getPostings "+ result[i]); //for each query term call getbydoc and getbyfreq to get its different postings list
                    Docindex.getbydoc(result[i]);
                    Termindex.getbyfreq(result[i]);
	               }
                //then call the taat and daat operations using their different indexes
                Termindex.taatand(strLine);
                Termindex.taator(strLine);
                Docindex.daatand(strLine);
                //Docindex2.daator(strLine);
                
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find the file: fileName");
        } catch (IOException e) {
            System.err.println("Unable to read the file: fileName");
        }
    }
    
    public static String[] getlinetoken(String s) {
        String[] result = s.split("\\s");
        return result;
    }
}

//----------------------------------------creates a different docindex for use by daator cuz the array gets mixed up otherwise to be safe--------------------
//not called till daator is fixed

class Docindex2 {
    public static BufferedReader br = null;
    public static BufferedReader cbr = null;
    public static String strLine = "";
    public static String newsize = "";
    public static int term_cnt=0;
    public static int lm=0;
    public static int qno;
    public static my_struct[] arraylist = new my_struct[100000];//get this size dynamically this array of structs will hold our term name,size and linked list
    public static k_struct [] klist = new k_struct[500];
    public static LinkedList<Lnode>[] lkarray = new LinkedList[100];//no of query terms could be huge so change into dynamic later
    
    
    public static void createdoc( String s) {
        //final my_struct[] arraylist = new my_struct[500];
        //fstring=args[0];
        try {
            br = new BufferedReader( new FileReader(s));
            while( (strLine = br.readLine()) != null) {
                //System.out.println(strLine);
                
                String[] result = strLine.split("\\\\");//should i make string empty again?
                arraylist[term_cnt] = new my_struct();
                
                arraylist[term_cnt].term=result[0];
                
                newsize=result[1].replace('c','0');
                
                int i = Integer.parseInt(newsize);
                //System.out.println(i);
                arraylist[term_cnt].size=i;
                
                String plnew = result[2].replaceAll("[\\p{Ps}\\p{Pe},m]", "");
                arraylist[term_cnt].plist=plnew;
                
                arraylist[term_cnt].l=manip.getlinkedlist(plnew);
                
                //arraylist[term_cnt].l.forEach(f -> System.out.println(f));
                //System.out.println(arraylist[term_cnt].l.get(0).freq);
                //System.out.println(firstvar.docid);
                term_cnt++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find the file: fileName");
        } catch (IOException e) {
            System.err.println("Unable to read the file: fileName");
        }
    } //end of createdoc
    
    /*public static void daator(String s){
     //LinkedList<Lnode>[] lkarray = new LinkedList[10];
     //LinkedList<Integer>[] vertex = new LinkedList[5];
     LinkedList<Lnode> mergell = new LinkedList<Lnode>();
     //LinkedList<Lnode> temp = new LinkedList<Lnode>();
     //LinkedList<Lnode>[] lkarrayn2 = new LinkedList[100];
     ArrayList<LinkedList> lk1 = new ArrayList<LinkedList>();
     Lnode tk=new Lnode();
     Lnode m=new Lnode();
     Lnode tsame=new Lnode();
     //lkarrayn2=lkarray.clone();
     my_struct[] arraylist3 = new my_struct[100000];
     int [] ptr = new int [500]; //array of pointers
     LinkedList<Lnode>[] lkor = new LinkedList[500];//same as ptr cuz depends on no of queries
     int in;
     //lkarrayn=lkarray.clone();
     arraylist3=arraylist.clone();
     String[] result1=Querymanip.getlinetoken(s); //somethin wrong?
     int qno1=result1.length;//no of query terms
    	System.out.println("no of query terms in daator" + qno1);
    	for(int i=0;i<qno1;i++)
     {
     for (int k=0; k<term_cnt; k++)
     {
     if((arraylist3[k].term).equals(result1[i]))
     {   System.out.println("atleast found a term" + arraylist3[k].term);
     //System.out.println("ist there a ll" + arraylist3[k].l.size());
     lkor[i]=arraylist3[k].l;
     break;
     
     }
     }
     }
    	
    	System.out.println("initial value of ptr array");
    	int mind;
    	int j;
    	for(int i=0;i<qno1;i++)
     ptr[i]=0;
	    printptr(ptr,qno1);
	    while(pointersincap(qno1,ptr,lkor)) {
     if(allnotequal(qno1,ptr,lkor))  //if elements not equal
     {
     int min=lkor[0].get(ptr[0]).docid;
     for(int i=1;i<qno1;i++)   //find min
     {
     if(lkor[i].get(ptr[i]).docid<lkor[i-1].get(ptr[i-1]).docid)
     {
     min=lkor[i].get(ptr[i]).docid;
     }
     }  //found min
     for(mind=0;mind<qno1;mind++) {
     if(lkor[mind].get(ptr[mind]).docid==min) {
     break;
     }
     }
     if
     ptr[mind]++;
     for(j=0;j<mergell.size();j++)
     {
     if(mergell.get(j).docid==lkor[mind].get(ptr[mind]).docid)
     {
     break;
     }
     }
     if(j==mergell.size()) {
     mergell.add(lkor[mind].get(ptr[mind]));
     }
     }
     else
     {
     
     }
	    }
	    
	    
    	/*System.out.println("HERE IS DAATOR");
    	for(int i=0;i<mergell.size();i++)
     System.out.print(mergell.get(i).docid + ", ");*/
    //}//end of daator*/
    //public static LinkedList<Lnode> find_min_daator(int qno,ArrayList<LinkedList> lk1)
    public static boolean pointersincap(int qno1, int [] ptr,LinkedList<Lnode>[] lkor) {
        int [] maxptr = new int [500];
        int flag=0;
        for(int i=0;i<qno1;i++) {
            maxptr[i]=lkor[i].size();
        }
        for(int j=0;j<qno1;j++) {
            if(ptr[j]<maxptr[j])
            flag=1;
        }
        if(flag==0)
        return false;
        return true;
    }
    public static boolean allnotequal(int qno1, int [] ptr,LinkedList<Lnode>[] lkor) {
        for(int x=0;x<qno1-1;x++) {
            if(lkor[x].get(ptr[x])!=lkor[x+1].get(ptr[x+1])) {
                return true;
            }
        }
        return false;
    }
    
    public static void printptr(int [] ptr,int qno1) {
        for(int i=0;i<qno1;i++)
        System.out.println(ptr[i]+" ,");
    }
    public static void printmerge(LinkedList<Lnode> mergell)
    {
        for(int i=0;i<mergell.size();i++)
        System.out.println("MERGELL HAS"+ mergell.get(i).docid);
    }
    
    
    
    
    
}//end of docindex2
//-------------------------------------------------------------creates index by DOCID and used for daatand-------------------------------------------------------


class Docindex {
    public static BufferedReader br = null;
    public static BufferedReader cbr = null;
    public static String strLine = "";
    public static String newsize = "";
    public static int term_cnt=0;
    public static int lm=0;
    public static int qno;
    public static my_struct[] arraylist = new my_struct[100000];//get this size dynamically
    public static k_struct [] klist = new k_struct[100000]; // will be used for topk again make this dynamic by no of strline read or arraylist later
    public static LinkedList<Lnode>[] lkarray = new LinkedList[100];//no of terms could be huge make into array list
    public static int c=0;
    
    public static void createdoc( String s) {
        try {
            br = new BufferedReader( new FileReader(s));
            while( (strLine = br.readLine()) != null) {
                //System.out.println(strLine);
                
                String[] result = strLine.split("\\\\");  //split string on \ using escape character
                arraylist[term_cnt] = new my_struct();
                
                arraylist[term_cnt].term=result[0];   //add first split as term name in our structure
                
                newsize=result[1].replace('c','0');
                
                int i = Integer.parseInt(newsize);
                //System.out.println(i);
                arraylist[term_cnt].size=i;           //get the size in proper integer format
                
                String plnew = result[2].replaceAll("[\\p{Ps}\\p{Pe},m]", "");// remove square brackets,m, and comma from last split
                arraylist[term_cnt].plist=plnew;     //put it in string plist of our structure not used
                
                arraylist[term_cnt].l=manip.getlinkedlist(plnew);  //pass this string to function manip that manipulates the string into a linked list then put it into the linked list field of my_struct
                
                term_cnt++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find the file: fileName");
        } catch (IOException e) {
            System.err.println("Unable to read the file: fileName");
        }
    }
    //finds the term in our struct array and displays docid ordered list
    
    public static void getbydoc(String s){
        //term_cnt=1;
        int k;
        for (k=0; k<term_cnt; k++)
        {
            if((arraylist[k].term).equals(s))
            break;
        }
        if(k==term_cnt)
        {System.out.println("term not found!");
            System.exit(0);
        }
        else {
            int len=arraylist[k].l.size();
            System.out.print("Ordered by doc IDs: ");
            if(len==1)
            {System.out.print(arraylist[k].l.get(0).docid);
                System.out.println();
            }
            else {
                for(int i=0;i<len-1;i++)
                System.out.print(arraylist[k].l.get(i).docid + ", ");
                System.out.print(arraylist[k].l.get(len-1).docid);
                System.out.println();}
        }
    }
    
    public static void gettopk(String s) { //l is no of top terms
        //public static k_struct [] klist = new k_struct[500];
        int temp;
        String temp1;
        for ( int k=0; k<term_cnt; k++)      //term_cnt variable in docindex for no of terms in our array
        {
            klist[k] = new k_struct();
            klist[k].termk=arraylist[k].term;   //read from main arraylist and put into new klist structure
            klist[k].sizek=arraylist[k].size;
            //System.out.println("term in new struct" + klist[k].termk + "new size"+ klist[k].sizek);
        }
        //int n = array.l;
        int p;
        for (int m = term_cnt; m >= 0; m--) {                         //sort klist structure
            for (int i = 0; i < term_cnt - 1; i++) {
                p = i + 1;
                if (klist[i].sizek < klist[p].sizek) {
                    
                    temp = klist[i].sizek;
                    temp1=klist[i].termk;
                    klist[i].sizek = klist[p].sizek;
                    klist[i].termk = klist[p].termk;
                    klist[p].sizek = temp;
                    klist[p].termk=temp1;
                    
                }
            }
            //printNumbers(array);
        }
        int d = Integer.parseInt(s);                     //display topK terms
        System.out.print("Result: ");
        for ( int k=0; k<d-1; k++)
        {
            
            System.out.print(klist[k].termk + ",");
        }
        System.out.println(klist[d-1].termk);
        
    }//end of method
    
    //---------------------------------------daat and is done here--------------------------------------------
    
    public static void daatand(String s){
        //final LinkedList<Lnode>[] lkarray = new LinkedList[10];
        //LinkedList<Integer>[] vertex = new LinkedList[5];
        LinkedList<Lnode> mergell = new LinkedList<Lnode>();
        LinkedList<Lnode> temp = new LinkedList<Lnode>();
        LinkedList<Lnode>[] lkarrayn = new LinkedList[100]; //array of linked lists make it dynamic no. of query terms can't be more than 100 for now
        long tStart, tEnd,tDelta;                        //start recording time
        double elapsedSeconds;
        tStart = System.currentTimeMillis();
        //ArrayList<LinkedList> lk = new ArrayList();
        //final int c=0;
        my_struct[] arraylist2 = new my_struct[100000];  //do everything with a stupid clone cuz things get messed up otherwise
        lkarrayn=lkarray.clone();
        arraylist2=arraylist.clone();
        String[] result=Querymanip.getlinetoken(s);   //split line on whitespace give resultant query terms did here on new copy to prevent mix ups
        qno=result.length;//no of query terms
        
        for(int i=0;i<qno;i++)
        for (int k=0; k<term_cnt; k++)
        {
            if((arraylist2[k].term).equals(result[i]))           //extract postings list
            {
                lkarrayn[i]=arraylist2[k].l;
                break;
            }
        }
        int min=lkarrayn[0].size();       //did min to check something remove later maybe
        for(int i=1;i<qno;i++) {
            if(lkarrayn[i].size()<min)
            {
                min=lkarrayn[i].size();
            }
        }
        //System.out.println("size of min linked list is" + min);
        while(checkfornull(lkarrayn)) {     //keep going till one list becomes null as you're popping
            if(first_not_same(qno,lkarrayn)) {
                //System.out.println("first elems not same");   //if all elements not same then find minimum and move pointer ahead or pop minimum same thing
                findmin(qno,lkarrayn).removeFirst();
                //System.out.println(node.docid);
            }
            else
            {
                //System.out.println("they are same");
                mergell.add(lkarrayn[0].get(0));  //if all same found then add that value to final list
                for(int x=0;x<qno;x++) {
                    lkarrayn[x].removeFirst();   //remove them from their list same as pointer going ahead
                }
                
            }
            /*for(int i=0;i<mergell.size();i++)
             System.out.print(mergell.get(i).docid + ", ");*/
        }
        tEnd = System.currentTimeMillis();
        tDelta = tEnd - tStart;
        elapsedSeconds = tDelta / 1000.0;  //end time taken then print values
        System.out.print("FUNCTION: docAtATimeQueryAnd");
        for(int i=0;i<qno-1;i++)
        {
            System.out.print(" " + result[i]+ ",");
        }
        System.out.print(" " + result[qno-1]);
        System.out.println();
        System.out.println(mergell.size() + " documents are found");
        System.out.println(c+" comparisons are made");
        System.out.println(elapsedSeconds+" seconds are used");
        int sz=mergell.size(); //if not 0
        if(sz==0)
        {System.out.print("Result: ");
            System.out.println("no docs found");
        }
        else if(sz==1)
        {System.out.print("Result: ");
            System.out.println(mergell.get(0).docid);}
        else {
            int t;
            int p;
            for (int m = sz; m >= 0; m--) {
                for (int i = 0; i < sz - 1; i++) {
                    p = i + 1;
                    if (mergell.get(i).docid > mergell.get(p).docid) {
                        
                        t = mergell.get(i).docid;
                        //temp1=klist[i].termk;
                        mergell.get(i).docid = mergell.get(p).docid;
                        mergell.get(p).docid = t;
                        //klist[p].termk=temp1;
                        
                    }
                }
                //printNumbers(array);
            }
            
            System.out.print("Result: ");
            for(int i=0;i<sz-1;i++)
            {System.out.print(mergell.get(i).docid + ", ");}
            System.out.println(mergell.get(sz-1).docid);}
        
    }
    
    public static LinkedList<Lnode> findmin(int qno,LinkedList<Lnode>[] lkarrayn) {
        int min=lkarrayn[0].get(0).docid;
        int x;
        for(x=1;x<qno;x++)
        {
            c++;       //comparisons done here to find min
            if(lkarrayn[x].get(0).docid<min)
            min=lkarrayn[x].get(0).docid;
            //lkarrayn[x].
        }
        for(x=0;x<qno;x++)
        {
            if(lkarrayn[x].get(0).docid==min)
            return lkarrayn[x];
            
        }
        return null;
    }
    
    public static boolean first_not_same(int qno,LinkedList<Lnode>[] lkarrayn) {    //check if the first elems are same or not
        //int x;
        int flag=0;
        //int i=0,j=0;
        for(int x=0;x<qno-1;x++) {
            //c++;
            if(lkarrayn[x].get(0).docid!=lkarrayn[x+1].get(0).docid)
            {
                flag=1;
                break;
            }
        }
        if(flag==1)
        return true;
        else
        return false;
    }
    public static boolean checkfornull(LinkedList<Lnode>[] lkarrayn) {
        int x;
        for(x=0;x<qno;x++) {
            if(lkarrayn[x].isEmpty())
            {
                //System.out.println("now empty");     //if a single list becomes empty
                return false;}
            //return 0;
        }
        return true;
        
    }
    
    
    
}//end of docindex class

//-------------------------------------------------create new index acc to term freq postings and taat ops done here---------------------------------------

class Termindex {
    public static BufferedReader br = null;
    public static String strLine = "";
    public static String newsize = "";
    public static int term_cnt=0;
    public static my_struct[] arraylist = new my_struct[100000];
    
    public static void createfreq(String s) {
        
        try {
            br = new BufferedReader( new FileReader(s));
            while( (strLine = br.readLine()) != null) {
                //System.out.println(strLine);
                
                String[] result = strLine.split("\\\\");//should i make string empty again?
                arraylist[term_cnt] = new my_struct();
                
                arraylist[term_cnt].term=result[0];
                
                newsize=result[1].replace('c','0');
                
                int i = Integer.parseInt(newsize);
                //System.out.println(i);
                arraylist[term_cnt].size=i;
                
                String plnew = result[2].replaceAll("[\\p{Ps}\\p{Pe},m]", "");
                arraylist[term_cnt].plist=plnew;
                
                arraylist[term_cnt].l=manip.getlinkedlist(plnew);
                
                term_cnt++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find the file: fileName");
        } catch (IOException e) {
            System.err.println("Unable to read the file: fileName");
        }
        
        for(int x=0;x<term_cnt;x++){                                //sort by freq and get new postings
            int n=arraylist[x].l.size();
            //int n = len;
            int k;
            for (int m = n; m >= 0; m--) {
                for (int i = 0; i < n - 1; i++) {
                    k = i + 1;
                    if (arraylist[x].l.get(i).freq< arraylist[x].l.get(k).freq) {
                        
                        int temp,temp1;
                        temp = arraylist[x].l.get(i).freq;
                        temp1 = arraylist[x].l.get(i).docid;
                        
                        arraylist[x].l.get(i).freq = arraylist[x].l.get(k).freq;
                        arraylist[x].l.get(i).docid = arraylist[x].l.get(k).docid;
                        arraylist[x].l.get(k).freq = temp;
                        arraylist[x].l.get(k).docid = temp1;
                        
                    }
                }
                //printNumbers(array);
            }
        }
        
    }
    public static void getbyfreq(String s){
        
        int k;
        for (k=0; k<term_cnt; k++)
        {
            if((arraylist[k].term).equals(s))
            break;
        }
        if(k==term_cnt)
        System.out.println("term not found!");
        else {
            int len=arraylist[k].l.size();
            System.out.print("Ordered by TF: ");
            for(int i=0;i<len-1;i++)
            System.out.print(arraylist[k].l.get(i).docid + ", ");
            System.out.print(arraylist[k].l.get(len-1).docid);
            System.out.println();
        }
        
        
    }
    public static LinkedList<Lnode> llbyfreq(String s){      //return linked list
        
        int k;
        for (k=0; k<term_cnt; k++)
        {
            if((arraylist[k].term).equals(s))
            break;
        }
        return arraylist[k].l;
        
    }
    
    public static void taatand(String s){
        LinkedList<Lnode>[] lkarray = new LinkedList[10];
        //LinkedList<Integer>[] vertex = new LinkedList[5];
        LinkedList<Lnode> mergell = new LinkedList<Lnode>();
        LinkedList<Lnode> m2 = new LinkedList<Lnode>();
        LinkedList<Lnode> temp = new LinkedList<Lnode>();
        String[] result=Querymanip.getlinetoken(s);
        ArrayList<Integer> fin = new ArrayList<Integer>();
        int qno=result.length;//no of query terms
        long tStart, tEnd,tDelta;
        double elapsedSeconds;
        tStart = System.currentTimeMillis();
        int c=0;
        for(int i=0;i<qno;i++)
        {
            lkarray[i]=llbyfreq(result[i]);  //here used function to get linked list cuz it wasnt messing up
            //int sz=lkarray[i].size();
            //System.out.println(sz);
        }
        int min=lkarray[0].size();            //find list with minimum size
        for(int i=0;i<qno;i++)
        if(lkarray[i].size()<min)
        {
            min=lkarray[i].size();}
        //System.out.println("MIN POST LIST IS"+ min);
        for(int i=0;i<qno;i++)
        if(lkarray[i].size()==min)
        mergell=(LinkedList<Lnode>) lkarray[i].clone();  //make that your starting list
        
        //System.out.println(mergell.get(0).docid);
        int x=0;
        int j;
        while(x<qno){
            for(int i=0;i<mergell.size();i++)
            {
                //System.out.println("mergell sx" + mergell.size());
                for(j=0;j<lkarray[x].size();j++) {
                    c++;
                    if(mergell.get(i).docid==lkarray[x].get(j).docid){                //if both are same then add to middle result and check for other terms
                        //System.out.println("aded to m2"+mergell.get(i).docid);
                        m2.add(mergell.get(i));
                        //System.out.println();
                        break;
                    }
                    /*if(j==lkarray[x+1].size())
                     lkarray[x+1].add(lkarray[x].get(i));*/
                }
                /*if(j==lkarray[x].size())
                 {mergell.remove(i);   //print mergell i to i-1size to check chang size?
                 i=i-1;}*/
            }
            
            
            mergell=(LinkedList<Lnode>) m2.clone();  //after one row is done make the merged list the one with clasing values
            
            m2.clear();  //clear this to find clashin values of next iteration
            x++;
        }
        //end of while
        //System.out.println("CLASHING DOC\\n");
        //System.out.println(mergell.size());
        tEnd = System.currentTimeMillis();
        tDelta = tEnd - tStart;
        elapsedSeconds = tDelta / 1000.0;
        System.out.print("FUNCTION: termAtATimeQueryAnd");   //print result did separately so arrays dont get mixed up
        for(int i=0;i<qno-1;i++)
        {
            System.out.print(" " + result[i]+ ",");
        }
        System.out.print(" " + result[qno-1]);
        System.out.println();
        System.out.println(mergell.size() + " documents are found");
        System.out.println(c+" comparisons are made");
        System.out.println(elapsedSeconds+" seconds are used");
        int sz=mergell.size(); //if not 0
        if(sz==0)
        {System.out.print("Result: ");
            System.out.println("no docs found");}
        else if(sz==1)
        {System.out.print("Result: ");
            System.out.println(mergell.get(0).docid);}
        else {
            int t;
            int p;
            for (int m = sz; m >= 0; m--) {
                for (int i = 0; i < sz - 1; i++) {
                    p = i + 1;
                    if (mergell.get(i).docid > mergell.get(p).docid) {
                        
                        t = mergell.get(i).docid;
                        //temp1=klist[i].termk;
                        mergell.get(i).docid = mergell.get(p).docid;
                        mergell.get(p).docid = t;
                        //klist[p].termk=temp1;
                        
                    }
                }
                
            }
            
            System.out.print("Result: ");
            for(int i=0;i<sz-1;i++)
            {System.out.print(mergell.get(i).docid + ", ");}
            System.out.println(mergell.get(sz-1).docid);}
        
        //System.out.print(mergell.get(0).docid);
        
    }//end of method
    
    public static void taator(String s){
        LinkedList<Lnode>[] lkarray = new LinkedList[10];
        
        LinkedList<Lnode> mergell = new LinkedList<Lnode>();
        //LinkedList<Lnode> temp = new LinkedList<Lnode>();
        String[] result=Querymanip.getlinetoken(s);
        int qno=result.length;//no of query terms
        //System.out.println("query terms are IN TAAT sizes!");
        int c=0;
        long tStart, tEnd,tDelta;
        double elapsedSeconds;
        tStart = System.currentTimeMillis();
        for(int i=0;i<qno;i++)
        {
            lkarray[i]=llbyfreq(result[i]);
            
        }
        mergell=(LinkedList<Lnode>) lkarray[0].clone();
        int x=0;
        int j;
        while(x<qno-1){      //keep finding terms that are not in next list and appending them to that list
            for(int i=0;i<lkarray[x].size();i++)
            {
                for(j=0;j<lkarray[x+1].size();j++) {
                    c++;
                    if(lkarray[x].get(i).docid==lkarray[x+1].get(j).docid)
                    break;
                    
                    /*if(j==lkarray[x+1].size())
                     lkarray[x+1].add(lkarray[x].get(i));*/
                }
                if(j==lkarray[x+1].size())
                lkarray[x+1].add(lkarray[x].get(i));   //since j reached max size found term that's not there so added it
            } x++;
        }
        
        tEnd = System.currentTimeMillis();  //end time and print
        tDelta = tEnd - tStart;
        elapsedSeconds = tDelta / 1000.0;
        System.out.print("FUNCTION: termAtATimeQueryOr");
        for(int i=0;i<qno-1;i++)
        {
            System.out.print(" " + result[i]+ ",");
        }
        System.out.print(" " + result[qno-1]);
        System.out.println();
        System.out.println(lkarray[x].size() + " documents are found");
        System.out.println(c+" comparisons are made");
        System.out.println(elapsedSeconds+" seconds are used");
        int sz=lkarray[x].size();
        System.out.print("Result: ");
        if(sz==1)
        System.out.println(lkarray[x].get(0).docid);
        else {
            int t;
            int p;
            for (int m = sz; m >= 0; m--) {
                for (int i = 0; i < sz - 1; i++) {
                    p = i + 1;
                    if (lkarray[x].get(i).docid > lkarray[x].get(p).docid) {
                        
                        t = lkarray[x].get(i).docid;
                        //temp1=klist[i].termk;
                        lkarray[x].get(i).docid = lkarray[x].get(p).docid;
                        lkarray[x].get(p).docid = t;
                        //klist[p].termk=temp1;
                        
                    }
                }
                //printNumbers(array);
            }
            for(int i=0;i<sz-1;i++)
            {System.out.print(lkarray[x].get(i).docid + ", ");}
            System.out.println(lkarray[x].get(sz-1).docid);
        }
    }//end of termindex class
}

//****************************************************MY STRUCTS************************************************
class my_struct {              //holds my term details put into dynamic arraylist 
    String term;
    int size;
    String plist;
    LinkedList<Lnode> l;
    
} 
class Lnode {                          //postings list will be of this type
    public int docid;
    int freq;
}
class k_struct {                //used for top k terms
    String termk;
    int sizek;
}
//---------------------------------------------------manipulates token after last \ into the postings list-------------------------------------------


class manip {
    public static LinkedList<Lnode> getlinkedlist(String s) {
        
        LinkedList<Lnode> linky = new LinkedList<Lnode>(); //the ll we'll return
        String[] s1 = s.split("\\s"); //split on space
        //Lnode[] nplist = new Lnode[100];
        for (int x=0; x<s1.length; x++)       //s1.length will be no of docs
        {
            
            
            //System.out.println(s1[x]);
            Lnode node= new Lnode();            //create new node with docid and frequency
            //nplist[docnt] = new Lnode();
            String[] s2 = s1[x].split("/");   //after splitting on / get the docid and frequency separately
            //System.out.println(s2[0]);
            //System.out.println(s2[1]);
            node.docid=Integer.parseInt(s2[0]); //do i need to int parse it here?
            node.freq=Integer.parseInt(s2[1]);
            linky.add(node);  //add to the linked list of that term
            
        }
        //linky.forEach(f -> System.out.println(f));
        return linky;  //return the ll;
    }	
    
}