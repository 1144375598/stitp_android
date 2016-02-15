package njupt.stitp.android.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class IconUtil {
	public static byte[] drawableToByte(Drawable icon) {
		//第一步，将Drawable对象转化为Bitmap对象
		Bitmap bmp = ((BitmapDrawable)icon).getBitmap();
		//第二步，声明并创建一个输出字节流对象
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		//第三步，调用compress将Bitmap对象压缩为PNG格式，第二个参数为PNG图片质量，第三个参数为接收容器，即输出字节流os
		bmp.compress(Bitmap.CompressFormat.PNG, 30, os);
		return os.toByteArray();
	}
	 public static Drawable byteToDrawable(byte[] blob){
		//调用BitmapFactory的解码方法decodeByteArray把字节数组转换为Bitmap对象
		 Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
		 //调用BitmapDrawable构造函数生成一个BitmapDrawable对象，该对象继承Drawable对象，所以在需要处直接使用该对象即可
		 BitmapDrawable bd = new BitmapDrawable(null,bmp);
		 return bd;
	 }
}
