package ps.abgabe;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Basic implementation of a stack using a
 * LinkedList to store data
 * 
 * @author Abbas ULUSOY
 *
 * @param <E> type of object stack holds
 */
public class DataStack<E> {
	
	/**
	 * Default constructor, initializes LinkedList
	 */
	public DataStack() {
		list=new LinkedList<E>();
	}
	
	/**
	 * Returns whether stack is empty or not
	 * @return whether stack is empty or not
	 */
	public boolean isEmpty() {
		return list.size()==0;
	}
	
	/**
	 * Pushes an object to the top of the stack
	 * @param e object to be pushed
	 * @return pushed object
	 */
	public E push( E e ) {
		list.addFirst(e);
		return list.getFirst();
	}
	
	/**
	 * Pops an object off the top of the stack
	 * @return popped object
	 */
	public E pop() {
		if(isEmpty())
			throw new EmptyStackException();
		return list.removeFirst();
	}
	
	/**
	 * Returns the object at the top of the stack
	 * without removing it
	 * @return first object on the stack
	 */
	public E peek() {
		if(isEmpty())
			throw new EmptyStackException();
		return list.getFirst();
	}
	
	/**
	 * get the size of list 
	 * @return size of list 
	 */
	public int size(){
		return list.size();
	}
	
	private LinkedList<E> list;

	/**
	 * @return remove object
	 */
	public E remove() {
		return list.remove();
	}

    /**
     * @return E 
     */
	public E removeFirst() {
		return list.removeFirst();
	}
	
    /**
     * @return E 
     */
	public Iterator<E> iterator() {
		return list.iterator();
	}
	
    /**
     * @param index
     * @return E
     */
    public E get(int index) {
        return list.get(index);
    }

    /**
     * @param index
     * @return E object
     */
    public E removeWithIndex(int index) {
        return list.remove(index);
    }

    /**
     * @param e object 
     * @return E
     */
    public E addLast(E e) {
        list.addLast(e);
        return list.getLast();
    }

    /**
     * @return get last element from stack
     */
    public E getLast() {
        return list.getLast();
    }

}