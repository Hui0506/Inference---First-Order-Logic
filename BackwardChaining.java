import java.io.*;
import java.util.*;

class Predicate
{
	String fnName;
	List <String> args;
	
	public Predicate()
	{
		fnName = "";
	//	args = new ArrayList<String>();
	}
	
	public Predicate(String pred) 
	{
		this();
		setPredicate(pred);		
	}

	//A(x),B(x,y)
	public void setPredicate(String singlePredicatestr)
	{
		String singlePredicate  = singlePredicatestr.trim();
		int indexOfStartBracket = singlePredicate.indexOf('(');
		int indexOfCloseBracket = singlePredicate.indexOf(')');

		this.fnName = singlePredicate.substring(0, indexOfStartBracket);
		String arguments[] = singlePredicate.substring(indexOfStartBracket+1,indexOfCloseBracket).split(",");
		//this.args = (ArrayList<String>)Arrays.asList(arguments);
	    this.args = new ArrayList<String>(Arrays.asList(arguments));
	}
}

class Sentence
{
	List<AllSubstitution> allSubst;// = new LinkedList<AllSubstitution>();
 	List <Predicate> lhs = new ArrayList<Predicate>();
	Predicate rhs;

	public void setlhsrhs(String lhs, String rhs)
	{
		setlhs(lhs);
		setrhs(rhs);
	}
	
	public void setlhs(String lhs)
	{
		String eachPredicate[] = lhs.split("\\^");
		for (String pred : eachPredicate) 
		{
			if(pred != null && pred != "")
			{
				this.lhs.add(new Predicate(pred));
			}
		}
	}
	
	public void setrhs(String rhs)
	{
		if(rhs != null && rhs != "")
		{
			this.rhs = new Predicate(rhs);
		}
	}
}

class Rule
{
	List <Sentence> sentences = new LinkedList<Sentence>();
}
class KnowledgeBase
{
	public Map<String, Rule> KB = new LinkedHashMap<String, Rule>();
	
	public void store(String sentence)
	{
		String splitSent[] = parseString(sentence);
		Sentence sent = new Sentence();
		if(splitSent.length == 1)
			sent.setlhsrhs("", splitSent[0]);
		else
			sent.setlhsrhs(splitSent[0], splitSent[1]);
		
		String key = sent.rhs.fnName;
		if(KB.containsKey(key))
		{
			Rule temp =   KB.get(key);
			temp.sentences.add(sent);
			KB.put(key, temp);
		}
		else
		{
			Rule r = new Rule();
			r.sentences.add(sent);
			KB.put(key, r);
		}
		
	}
	
	public String[] parseString(String sentence)
	{
		String splitSent[] = sentence.split("=>");					
		return splitSent;
	}
	
}

class AllSubstitution
{
	List<Substitution> SubstList = new LinkedList<Substitution>();
	boolean status = false;
}

class Substitution
{
	String var;
	String val;
	
	public Substitution(String var, String val)
	{
		setValues(var, val);
	}
	
	public void setValues(String var, String val)
	{
		this.var = var;
		this.val = val;
	}
}

class Inference
{
	KnowledgeBase KBObj = new KnowledgeBase();
	List<Predicate> queries = new ArrayList<Predicate>();
	//Map<String, LinkedList<AllSubstitution>> substList = new LinkedHashMap<String, LinkedList<AllSubstitution>>();
	
	public Inference()
	{
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("output_1.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	    System.setOut(out);
	}
	
	public void readInput(String fileName)
	{
		int i1 = 0;
		try 
		{
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			int noOfQueries = Integer.parseInt(br.readLine());
			queries = new ArrayList<Predicate>();
			for(int i=0; i<noOfQueries; i++)
			{
				queries.add(new Predicate(br.readLine()));				
			}
			
			int noOfSentences = Integer.parseInt(br.readLine());
			
			for(i1=0; i1<noOfSentences; i1++)
			{
				KBObj.store(br.readLine());
			}		
		}  
		catch (Exception e) 
		{
			System.out.println(e.getMessage() + "sent number:" + i1);
		}
	}
	
	public AllSubstitution UNIFY(Predicate x, Predicate y, Sentence sent)
	{
		//1. Find an array of substitutiion tsubstList
		//2. Now the linked hashmap contains all the array of substitution.
		//3. If 1 is contained in 2 then return false and delete from hash map
		//else add in the map.
		
		//This is the returned value.
		AllSubstitution tAllSubst = new AllSubstitution();
		//This is part of return value. This is part of tAllSubst.
		List<Substitution> tSubstList = new LinkedList<Substitution>();
		//Current subst.
		Substitution s = null;
		//Unification success or failure.
		// 1. Fails when same sentence comes with same substitution.
		boolean tStatus = false;
		
		boolean available = true;
		//Just make sure length of args of x and y are same. Also just check if they are same fnName.
		for(int i=0; i<x.args.size(); i++)
		{	
			//x and y both can be variable. I don't know what to do.
			if(isVariable(x,i))
			{
			    s = new Substitution(x.args.get(i), y.args.get(i));	
			    tStatus = true;
			}
			
			//x and y both can be variable. I don't know what to do.
			else if(isVariable(y,i))
			{
			    s = new Substitution(y.args.get(i), x.args.get(i));
				//tSubstList.add(s);
				tStatus = true;
			} 
			
			//x and y both are not variables. Just make status as true.  
			else if(x.args.get(i).equals(y.args.get(i)))
			{
				s = new Substitution(y.args.get(i), x.args.get(i));
				//tSubstList.add(s);
				tStatus = true;
			}				
			else
			{
				s = null;
				tStatus = false;
				break;
			}
			
			/*
			for(int j=0; j<tlist.size();j++)
			{
				Substitution tSub = tlist.get(j);
				if(tSub!=null && tSub.var.equals(s.var) && tSub.val.equals(s.val))
				{
					tStatus = false;
					available = true;
					tlist.remove(j);
					break;
				}				
			}
			*/
			if(s!=null)
			{
				tSubstList.add(s);
			}
			/*
			if(!available)
			{
				tlist.add(s);
			}
			*/
		}
		//AllSubstitution temp = new AllSubstitution();
		//temp.SubstList = tlist;
		
		//LinkedList<AllSubstitution> tglobalSubst = substList.get(x.fnName);
		//if(tglobalSubst == null)
			//tglobalSubst = new AllSubstitution();
		//List<Substitution> tlist=tglobalSubst.SubstList;
		
		
		//check tstatus is true or false. If false return.
		if(tStatus)
		{
			Map<String, String> temp = new HashMap<String, String>();	
//
			for (Substitution substitution : tSubstList) {
				
				String match = temp.get(substitution.var);
				if(match!=null && !match.equals("") && !substitution.val.equals(match))
				{
					tStatus= false;
					break;
				}
				else
				{
					temp.put(substitution.var, substitution.val);
				}
					
			}
			
			//now check if all elements of tSubstList are present in global hashmap.
			if(sent.allSubst == null || sent.allSubst.size()==0)
			{	
				if(sent.allSubst == null)
				{
					sent.allSubst = new LinkedList<AllSubstitution>();
				}
				tAllSubst.SubstList = tSubstList;
				tAllSubst.status = tStatus;
				if(sent.lhs.size()!=0)
				sent.allSubst.add(tAllSubst);
			}
			else
			{

				for (AllSubstitution lsubst : sent.allSubst) 
				{
					available = true;
					for(int i = 0;i<tSubstList.size();i++)
					 {
						if(!tSubstList.get(i).var.equals(lsubst.SubstList.get(i).var) || !tSubstList.get(i).val.equals(lsubst.SubstList.get(i).val))
						{
							available = false;
							break ;
						}					
					 }	
					if(available)
					{
						tStatus = false;
						//sent.allSubst.remove(lsubst);
						break;
					}
				}
				
				if(!available)
				{
					tAllSubst.SubstList = tSubstList;
					tAllSubst.status = tStatus;
					sent.allSubst.add(tAllSubst);
				}
			}
		}
		else
		{
		tAllSubst.SubstList = tSubstList;
		tAllSubst.status = tStatus;	
		}
		return tAllSubst;
	}
	
	public boolean isVariable(Predicate x, int index)
	{
		char arg = x.args.get(index).charAt(0);
		if(arg>='a' && arg<='z')
			return true;	
		return false;
	}
			
	public void FOL_BC_ASK()
	{		
		for (Predicate predicate : queries) {
	
			try
			{
				for (Map.Entry<String, Rule> entry : KBObj.KB.entrySet())
				{
					for (Sentence sent : entry.getValue().sentences) {
						sent.allSubst = null;
					}
				}
				AllSubstitution obj = FOL_BC_OR(predicate, null ,0);
				if(obj==null)
					System.out.println("FALSE");
				else
				System.out.println(obj.status?"TRUE":"FALSE");
				
				//substList = new LinkedHashMap<String, LinkedList<AllSubstitution>>();		
			}
			catch(Exception e)
			{
				System.out.println("FALSE");				
			}
		}
	}
	
	public AllSubstitution FOL_BC_OR(Predicate goal, AllSubstitution theta, int index)
	{
		//the key is the function name of query. H(x), H is key.
		String key = goal.fnName;
		
		//get all the sentence which has conclusion as goal.
		List<Sentence> sents = (KBObj.KB.get(key)==null)? null:KBObj.KB.get(key).sentences;		
		if(sents!=null)
		{
			//Iterate through all the sentence which have conclusion as sentence.
			//for (Sentence sentence : sents) {
			for(int i = index; i<sents.size(); i++)
			{
				Sentence sentence = sents.get(i);
				AllSubstitution subst = UNIFY(sentence.rhs, goal, sentence);
				
				AllSubstitution thetaPrime = FOL_BC_AND(sentence.lhs, subst);
				if(thetaPrime!=null && thetaPrime.status)
				{
					return thetaPrime;
				}
			}
		}
		AllSubstitution t = new AllSubstitution();
		t.status = false;
		return t;
	}
	
	public AllSubstitution FOL_BC_AND(List <Predicate> goal, AllSubstitution theta)
	{
		//
		if(theta.status == false)
			return theta;
		else if(goal.size() == 0)
			return theta;
		else
		{
			Predicate first = goal.get(0); //n
			
			first = substFromTheata(theta, first);
			
			int index=0;
					
			
			int tSize = (KBObj.KB.get(first.fnName) == null) ? 0 :(KBObj.KB.get(first.fnName).sentences.size()-1);
			while(index<=tSize)
			{
				AllSubstitution thetaPrime = FOL_BC_OR(first, theta, index++);
				
				if(thetaPrime.status && goal.size()>1)
				{
					List<Predicate> rest = new ArrayList<Predicate>(((ArrayList<Predicate>)goal).subList(1, goal.size()));
					
					AllSubstitution thetaDoublePrime = FOL_BC_AND(rest, sumUpTheta(theta,thetaPrime));
					
					if(thetaDoublePrime.status)
					return thetaDoublePrime;
				}
				
				else return thetaPrime;
	
			}
			AllSubstitution t = new AllSubstitution();
			t.status = false;
			return t;
		}
	}

	public AllSubstitution sumUpTheta(AllSubstitution x, AllSubstitution y)
	{
		List<Substitution> newList = new LinkedList<Substitution>(x.SubstList);
		newList.addAll(y.SubstList);
		AllSubstitution newObj = new AllSubstitution();
		newObj.SubstList = newList;
		newObj.status = y.status;
		
		return newObj;
	}
	private Predicate substFromTheata(AllSubstitution theta, Predicate first) 
	{
		Predicate newGoal = new Predicate();
		newGoal.fnName = first.fnName;
		newGoal.args = new ArrayList<String>();
				
		List<Substitution> substList = theta.SubstList;
		List<String> args = first.args;
		
		boolean found;
		for (String str : args) 
		{
			found = false;		
			for (Substitution substitution : substList) 
			{
				
				if(str.equals(substitution.var))
				{
					newGoal.args.add(substitution.val);
					found = true;
					break;
				}
			}
			
			if(!found)
			{
				newGoal.args.add(str);
			}
		}
		return newGoal;
	}
}
public class BackwardChaining {
	public static void main(String[] args) {
		Inference ir = new Inference();
		//ir.readInput("C:\\Users\\Nimesh\\workspace\\AI-BackwardChaining\\src\\input_1.txt");
		ir.readInput(args[1]);

		ir.FOL_BC_ASK();
	}
}
