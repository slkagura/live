import androidx.annotation.NonNull;

import org.junit.Test;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/3/27 20:29
 */
public class TriangleExtendTest {
    private static void extend(StringBuilder builder, Point original, Point target, double atan) {
        // 计算 length
        double length = Math.sqrt(Math.pow(target.mX, 2) + Math.pow(target.mY, 2));
        builder.append("original length: ").append(length).append(System.lineSeparator());
        // 计算 sin
        double sin = Math.sin(atan);
        builder.append("sin: ").append(sin).append(System.lineSeparator());
        // 计算 cos
        double cos = Math.cos(atan);
        builder.append("cos: ").append(cos).append(System.lineSeparator());
        // 计算 extend point 1
        double extendX1 = target.mX + cos * length;
        double extendY1 = target.mY + sin * length;
        builder.append("extend point 1: (").append(extendX1).append(", ").append(extendY1).append(")").append(System.lineSeparator());
        // 计算 extend length 1
        double extendLength1 = Math.sqrt(Math.pow(extendX1 - target.mX, 2) + Math.pow(extendY1 - target.mY, 2));
        builder.append("extend length 1: ").append(extendLength1).append(System.lineSeparator());
        // 计算 extend point 2
        double extendX2 = original.mX - cos * length;
        double extendY2 = original.mX - sin * length;
        builder.append("extend point 2: (").append(extendX2).append(", ").append(extendY2).append(")").append(System.lineSeparator());
        // 计算 extend length 2
        double extendLength2 = Math.sqrt(Math.pow(extendX2 - original.mX, 2) + Math.pow(extendY2 - original.mY, 2));
        builder.append("extend length 2: ").append(extendLength2).append(System.lineSeparator());
        // 计算 extend length
        double extendLength = Math.sqrt(Math.pow(extendX2 - extendX1, 2) + Math.pow(extendY2 - extendY1, 2));
        builder.append("extend length: ").append(extendLength).append(System.lineSeparator());
    }
    
    private static void calcTan(StringBuilder builder, Point original, Point target) {
        // 初始点位
        builder.append("original point: ").append(original).append(System.lineSeparator());
        builder.append("target point: ").append(target).append(System.lineSeparator());
        
        // 计算 tan
        double atan = Math.atan((target.mY - original.mY) / (target.mX - original.mX));
        builder.append("atan: ").append(atan).append(System.lineSeparator());
        // 计算 tan degrees
        double atanDegrees = Math.toDegrees(atan);
        builder.append("atan degrees: ").append(atanDegrees).append(System.lineSeparator());
        // 计算延长
        extend(builder, original, target, atan);
    }
    
    private static void calcTan2(StringBuilder builder, Point original, Point target) {
        // 初始点位
        builder.append("original point: ").append(original).append(System.lineSeparator());
        builder.append("target point: ").append(target).append(System.lineSeparator());
        
        // 计算 tan2
        double atan2 = Math.atan2(target.mY - original.mY, target.mX - original.mX);
        builder.append("atan2: ").append(atan2).append(System.lineSeparator());
        // 计算 tan2 degrees
        double atan2Degrees = Math.toDegrees(atan2);
        builder.append("atan2 degrees: ").append(atan2Degrees).append(System.lineSeparator());
        // 计算延长
        extend(builder, original, target, atan2);
    }
    
    private static void transform(StringBuilder builder, Point original, Point target) {
        // 平移
        builder.append("transform ").append(original).append(target);
        Point newTarget = new Point(target.mX - original.mX, target.mY - original.mY);
        Point newOriginal = new Point(0, 0);
        builder.append(" -> ").append(newOriginal).append(newTarget).append(System.lineSeparator());
        
        // 计算 tan2
        double atan2 = Math.atan2(newTarget.mY - newOriginal.mY, newTarget.mX - newOriginal.mX);
        builder.append("atan2: ").append(atan2).append(System.lineSeparator());
        // 计算 tan2 degrees
        double atan2Degrees = Math.toDegrees(atan2);
        builder.append("atan2 degrees: ").append(atan2Degrees).append(System.lineSeparator());
        
        // 计算 length
        double length = Math.sqrt(Math.pow(newTarget.mX, 2) + Math.pow(newTarget.mY, 2));
        builder.append("original length: ").append(length).append(System.lineSeparator());
        // 计算 sin
        double sin = Math.sin(atan2);
        builder.append("sin: ").append(sin).append(System.lineSeparator());
        // 计算 cos
        double cos = Math.cos(atan2);
        builder.append("cos: ").append(cos).append(System.lineSeparator());
        // 计算 extend point 1
        Point newExtendTarget = new Point(newTarget.mX + cos * length, newTarget.mY + sin * length);
        builder.append("extend point 1: ").append(newExtendTarget).append(System.lineSeparator());
        // 计算 extend length 1
        double extendLength1 = Math.sqrt(Math.pow(newExtendTarget.mX - newTarget.mX, 2) + Math.pow(newExtendTarget.mY - newTarget.mY, 2));
        builder.append("extend length 1: ").append(extendLength1).append(System.lineSeparator());
        // 计算 extend point 2
        Point newExtendOriginal = new Point(newOriginal.mX - cos * length, newOriginal.mX - sin * length);
        builder.append("extend point 2: ").append(newExtendOriginal).append(System.lineSeparator());
        // 计算 extend length 2
        double extendLength2 = Math.sqrt(Math.pow(newExtendOriginal.mX - newOriginal.mX, 2) + Math.pow(newExtendOriginal.mY - newOriginal.mY, 2));
        builder.append("extend length 2: ").append(extendLength2).append(System.lineSeparator());
        // 计算 extend length
        double extendLength = Math.sqrt(Math.pow(newExtendOriginal.mX - newExtendTarget.mX, 2) + Math.pow(newExtendOriginal.mY - newExtendTarget.mY, 2));
        builder.append("extend length: ").append(extendLength).append(System.lineSeparator());
        
        // 恢复平移
        builder.append("reset ").append(newExtendOriginal).append(newTarget);
        Point resetNewExtendTarget = new Point(newExtendTarget.mX + original.mX, newExtendTarget.mY + original.mY);
        Point resetNewExtendOriginal = new Point(newExtendOriginal.mX + original.mX, newExtendOriginal.mY + original.mY);
        builder.append(" -> ").append(resetNewExtendOriginal).append(resetNewExtendTarget).append(System.lineSeparator());
        // 计算 transform extend length
        double transformExtendLength = Math.sqrt(Math.pow(resetNewExtendOriginal.mX - resetNewExtendTarget.mX, 2) + Math.pow(resetNewExtendOriginal.mY - resetNewExtendTarget.mY, 2));
        builder.append("reset extend length: ").append(extendLength).append(System.lineSeparator());
    }
    
    @Test
    public void normalExtend() {
        StringBuilder builder = new StringBuilder();
        builder.append("------------ Normal Extend ------------").append(System.lineSeparator());
        Point original = new Point(0, 0);
        // (4, 3)
        Point target = new Point(4, 3);
        calcTan2(builder, original, target);
        
        // (-4, 3)
        builder.append(System.lineSeparator());
        target.set(-4, 3);
        calcTan2(builder, original, target);
        
        // (-4, -3)
        builder.append(System.lineSeparator());
        target.set(-4, -3);
        calcTan2(builder, original, target);
        
        // (4, -3)
        builder.append(System.lineSeparator());
        target.set(4, -3);
        calcTan2(builder, original, target);
        builder.append("---------------------------------------");
        System.out.println(builder);
    }
    
    @Test
    public void specialExtend() {
        StringBuilder builder = new StringBuilder();
        builder.append("------------ Special Extend -----------").append(System.lineSeparator());
        // (4, 0)(0, 3)
        Point original = new Point(0, 3);
        Point target = new Point(4, 0);
        transform(builder, original, target);
        
        // (-4, 0)(0, 3)
        builder.append(System.lineSeparator());
        original.set(0, 3);
        target.set(-4, 0);
        transform(builder, original, target);
        
        // (-4, 0)(0, -3)
        builder.append(System.lineSeparator());
        original.set(0, -3);
        target.set(-4, 0);
        transform(builder, original, target);
        
        // (4, 0)(0, -3)
        builder.append(System.lineSeparator());
        original.set(0, -3);
        target.set(4, 0);
        transform(builder, original, target);
        builder.append("---------------------------------------");
        System.out.println(builder);
    }
    
    private static class Point {
        double mX;
        
        double mY;
        
        public Point(double x, double y) {
            mX = x;
            mY = y;
        }
        
        @NonNull
        @Override
        public String toString() {
            return "(" + mX + ", " + mY + ")";
        }
        
        public void set(double x, double y) {
            mX = x;
            mY = y;
        }
    }
}
