/** SJFSchedulingAlgorithm.java
 * 
 * A shortest job first scheduling algorithm.
 *
 * @author: Charles Zhu
 * Spring 2016
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

import com.jimweller.cpuscheduler.Process;

public class SJFSchedulingAlgorithm extends BaseSchedulingAlgorithm implements OptionallyPreemptiveSchedulingAlgorithm {
    private ArrayList<Process> jobs;
    class SJFSComparator implements Comparator<Process> {
        public int compare(Process p1, Process p2) {
            if (p1.getBurstTime() != p2.getBurstTime()) {
                return Long.signum(p1.getBurstTime() - p2.getBurstTime());
            }
            return Long.signum(p1.getPID() - p2.getPID());
        }
    }
    private boolean isPreemptive;
    SJFSComparator comparator = new SJFSComparator();
    SJFSchedulingAlgorithm(){
        // Fill in this method
        /*------------------------------------------------------------*/
        activeJob=null;
        jobs = new ArrayList<Process>();
        isPreemptive=false;  //set preemp default to false

        /*------------------------------------------------------------*/
    }

    /** Add the new job to the correct queue.*/
    public void addJob(Process p){
        // Remove the next lines to start your implementation
        //throw new UnsupportedOperationException();
        
        // Fill in this method
        /*------------------------------------------------------------*/
        jobs.add(p);
        Collections.sort(jobs,comparator);

        /*------------------------------------------------------------*/
    }
    
    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p){
        // Remove the next lines to start your implementation
        //throw new UnsupportedOperationException();
        
        // Fill in this method
        /*------------------------------------------------------------*/
        if (p == activeJob)
            activeJob = null;

        return jobs.remove(p);


        /*------------------------------------------------------------*/
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
    when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
        throw new UnsupportedOperationException();
    }

    /** Returns the next process that should be run by the CPU, null if none available.*/
    public Process getNextJob(long currentTime){
        // Remove the next lines to start your implementation
       // throw new UnsupportedOperationException();
        
        // Fill in this method
        /*------------------------------------------------------------*/
        Process next=null;
        if (!isPreemptive && !isJobFinished())   //the scheduler is not preemptive --> job has to finish
            return activeJob;
        if (jobs.size()>0)
            next=jobs.get(0); //get the first job in the jobs queue (sorted)
        activeJob=next;
        //System.out.println("current time: "+ currentTime +", active job: "+activeJob.getPID());
        return activeJob;
        /*------------------------------------------------------------*/
    }

    public String getName(){
        return "Shortest Job First";
    }

    /**
     * @return Value of preemptive.
     */
    public boolean isPreemptive(){
        // Remove the next lines to start your implementation
        //throw new UnsupportedOperationException();
        
        // Fill in this method
        /*------------------------------------------------------------*/
        return isPreemptive;


        /*------------------------------------------------------------*/
    }
    
    /**
     * @param v  Value to assign to preemptive.
     */
    public void setPreemptive(boolean  v){
        // Remove the next lines to start your implementation
        //throw new UnsupportedOperationException();
        
        // Fill in this method
        /*------------------------------------------------------------*/
        isPreemptive=v;


        /*------------------------------------------------------------*/
    }
    
}