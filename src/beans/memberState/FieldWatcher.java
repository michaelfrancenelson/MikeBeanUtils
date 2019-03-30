package beans.memberState;

import java.lang.reflect.Field;

public interface FieldWatcher<T>
{
	public String  getStringVal(T t);
	public int     getIntVal(T t);
	public double  getDoubleVal(T t);
	public boolean getBoolVal(T t);
	
	public String getFieldName();
	public String getDisplayName();

	public void setWatchedField(Field f);
	public void setWatchedField(String fieldName);
	
	public Field getField();
	
	
	
	
	
	public static class ObjectFieldWatcher<T> implements FieldWatcher<T>
	{
		
		private String dblFmt;
		private Class<T> clazz;
		
		
		
		
		
		

		@Override
		public String getStringVal(T t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getIntVal(T t) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double getDoubleVal(T t) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean getBoolVal(T t) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getFieldName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDisplayName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setWatchedField(Field f) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setWatchedField(String fieldName) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Field getField() {
			// TODO Auto-generated method stub
			return null;
		}
		
		
		
		
	}
	
	
	
	
}
