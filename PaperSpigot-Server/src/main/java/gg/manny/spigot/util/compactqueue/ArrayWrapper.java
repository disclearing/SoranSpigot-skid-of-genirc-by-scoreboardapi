package gg.manny.spigot.util.compactqueue;

import java.io.Serializable;

public abstract interface ArrayWrapper<E> extends Cloneable, Serializable {
	
  public abstract E get(int paramInt);
  
  public abstract void set(int paramInt, E paramE);
  
  public abstract ArrayWrapper<E> newInstance(int paramInt);
  
  public abstract ArrayWrapper<E> newInstance(E... paramVarArgs);
}