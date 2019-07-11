/** FCFSSchedulingAlgorithm.java
 * 
 * A first-come first-served scheduling algorithm.
 * The current implementation will work without memory management features
 *
 */
package com.jimweller.cpuscheduler;

import java.awt.*;
import java.util.*;
import java.util.List;

public class FCFSSchedulingAlgorithm extends BaseSchedulingAlgorithm {

	private ArrayList<Process> jobs;
	// Add data structures to support memory management
	/*------------------------------------------------------------*/
	private TreeMap<Integer,Integer> allocatedMem;   // store memory as <base index, size of allocated block>
	private TreeMap<Integer,Integer> freeMem;
	private TreeMap<Long,Integer> process_pos; //map <pid, start index of mem block it occupies>
	/*------------------------------------------------------------*/

	class FCFSComparator implements Comparator<Process> {
		public int compare(Process p1, Process p2) {
			if (p1.getArrivalTime() != p2.getArrivalTime()) {
				return Long.signum(p1.getArrivalTime() - p2.getArrivalTime());
			}
			return Long.signum(p1.getPID() - p2.getPID());
		}
	}
	private boolean is_First;
	FCFSComparator comparator = new FCFSComparator();

	FCFSSchedulingAlgorithm() {
		activeJob = null;
		jobs = new ArrayList<Process>();
		is_First=true; //default set mem management to First Fit
		// Initialize memory
		/*------------------------------------------------------------*/
		allocatedMem=new TreeMap<Integer,Integer>();
		freeMem=new TreeMap<Integer, Integer>();
		freeMem.put(0,380);   //initialize initial free space to be 380
		process_pos=new TreeMap<Long,Integer>();
		/*------------------------------------------------------------*/

	}

	private void coalescing(int free_block){
		List<Integer> keyset=new ArrayList<>(freeMem.keySet());
		int current=keyset.indexOf(free_block);
		int current_pos=keyset.get(current);
		int last_pos=-1;
		int next_pos=-1;
		int next_size=-1;
		int last_size=-1;
		if (current>0) {
			last_pos=keyset.get(current-1);
			last_size=freeMem.get(last_pos);
		}
		if (current<keyset.size()-1) {
			next_pos = keyset.get(current + 1);
			next_size=freeMem.get(next_pos);
		}

		int curr_size=freeMem.get(current_pos);

		if (last_pos!=-1 && last_pos+last_size==current_pos){
			freeMem.remove(last_pos);
			freeMem.remove(current_pos);
			if (next_pos!=-1 && current_pos+curr_size==next_pos){
				freeMem.remove(next_pos);
				freeMem.put(last_pos,last_size+curr_size+next_size);
				return;
			}
			freeMem.put(last_pos,last_size+curr_size);
		}
		else if (next_pos!=-1&& current_pos+curr_size==next_pos){
			freeMem.remove(current_pos);
			freeMem.remove(next_pos);
			freeMem.put(current_pos,curr_size+next_size);
		}


	}
	private void show_tree(TreeMap<Integer,Integer> t){
		for (Map.Entry<Integer,Integer> i :t.entrySet()){
			//System.out.println(i.getKey()+" , " +i.getValue());
		}
		//System.out.println(" ");
	}

	/** Add the new job to the correct queue. */
	public void addJob(Process p) {
	//System.out.println("Adding process "+p.getPID());
	// Check if any memory is available 
	/*------------------------------------------------------------*/
	if(is_First){
		List<Integer> keyset=new ArrayList<>(freeMem.keySet());  //!!!POSSIBLE CONCURRENT MODIFICATION EXCEPTION; avoid this by wrap it around arrayList
		for (Integer key :keyset){   //loop through available memory in order of memory location
			Integer size=freeMem.get(key);
			if (size>=p.getMemSize()){
				allocatedMem.put(key,(int)p.getMemSize());
				freeMem.remove(key);
				if (size-(int)p.getMemSize()!=0) freeMem.put(key+(int)p.getMemSize(),size-(int)p.getMemSize());
				//System.out.println("MM select starting index: "+key+", size: "+p.getMemSize());

				process_pos.put(p.getPID(),key);

				//System.out.println("free memory alloc as followed: ");
				show_tree(freeMem);
				//System.out.println("allocated memory as followed: ");
				show_tree(allocatedMem);
				break;
			}
		}
		if (!process_pos.containsKey(p.getPID())) {
			//System.out.println("Failed to find available memory block \n");
			p.setIgnore(true);
			return;
		}
	}
	else{   //best_fit
		int best_key=-1; //store starting index of best hole
		int best_leftover=Integer.MAX_VALUE; // store smallest leftover so far
		for (Integer key :freeMem.keySet()){   //loop through available memory in order of memory location
			Integer size=freeMem.get(key);
			if (size>=p.getMemSize() && (size-(int)p.getMemSize())< best_leftover){
				best_leftover=size-(int)p.getMemSize();
				best_key=key;
			}
		}
		if (best_key!=-1){
			allocatedMem.put(best_key,(int)p.getMemSize());
			freeMem.remove(best_key);
			if(best_leftover!=0) freeMem.put(best_key+(int)p.getMemSize(),best_leftover);
			process_pos.put(p.getPID(),best_key);
			//System.out.println("MM select starting index: "+best_key+", size: "+p.getMemSize());
			//System.out.println("free memory alloc as followed: ");
			show_tree(freeMem);
			//System.out.println("allocated memory as followed: ");
			show_tree(allocatedMem);
		}
		else {
			//System.out.println("Failed to find available memory block \n");
			p.setIgnore(true);
			return;
		}
	}

	/*------------------------------------------------------------*/

	// If enough memory is not available then don't add it to queue 
	// {
	// 	p.setIgnore(true);
	// 	return;
	// }

		jobs.add(p);
		Collections.sort(jobs, comparator);
	}

	/** Returns true if the job was present and was removed. */
	public boolean removeJob(Process p) {
		if (p == activeJob)
			activeJob = null;

		// In case memory was allocated, free it
		/*------------------------------------------------------------*/

		if (process_pos.containsKey(p.getPID())) {
			//System.out.println("Removing job "+p.getPID());
			int start_ind = process_pos.get(p.getPID());
			allocatedMem.remove(start_ind); //remove the process memblock from
			freeMem.put(start_ind, (int) p.getMemSize());
			coalescing(start_ind);
			process_pos.remove(p.getPID());

			//System.out.println("MM removing starting index: " + start_ind + ", size: " + p.getMemSize());
			//System.out.println("free memory alloc as followed: ");
			show_tree(freeMem);
			//System.out.println("allocated memory as followed: ");
			show_tree(allocatedMem);
		}
		/*------------------------------------------------------------*/
		return jobs.remove(p);
	}

	/**
	 * Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such
	 * as when switching to another algorithm in the GUI
	 */
	public void transferJobsTo(SchedulingAlgorithm otherAlg) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the next process that should be run by the CPU, null if none
	 * available.
	 */
	public Process getNextJob(long currentTime) {
		Process earliest = null;

		if (!isJobFinished())
			return activeJob;
		if (jobs.size() > 0)
			earliest = jobs.get(0);
		activeJob = earliest;
		//System.out.println("current time: "+ currentTime +", active job: "+activeJob.getPID());
		return activeJob;
	}

	public String getName() {
		return "First-Come First-Served";
	}

	public void setMemoryManagment(String v) {
		// Modify class to support memory management

		if (v.equals("FIRST")){
			is_First=true;
			//System.out.println("Set MM mode to: FIRST FIT");
		}
		else {
			is_First=false;
			//System.out.println("Set MM mode to: BEST FIT");
		}
	}
}