package com.linhh.lkdbhelper.plugin.apply;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by Linhh on 2017/11/13.
 */

public class UpdateToDBApply implements ILKDBPlguinApply,Opcodes {
    @Override
    public void apply(MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/lhh/lkdb/LKDBModelManager", "updateToDB", "(Lcom/lhh/lkdb/ILKDBModel;Ljava/lang/Object;)Z", false);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }
}
