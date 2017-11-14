package com.linhh.lkdbhelper.plugin.apply;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by Linhh on 2017/11/13.
 */

public class DeleteToDBApply implements ILKDBPlguinApply,Opcodes {
    @Override
    public void apply(MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "com/lhh/lkdb/LKDBModelManager", "deleteToDB", "(Lcom/lhh/lkdb/ILKDBModel;)Z", false);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }
}
