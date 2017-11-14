package com.linhh.lkdbhelper.plugin;

import com.linhh.lkdbhelper.plugin.apply.ILKDBPlguinApply;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linhh on 2017/11/13.
 */

public class LKDBHelperClassVisitor  extends ClassVisitor {
    public String mClazzName;
    private boolean mIsLKModel = false;

    public LKDBHelperClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        if(interfaces != null){
            for(String string: interfaces){
                if(string.equals("com/lhh/lkdb/ILKDBModel")){
                    mIsLKModel = true;
                    break;
                }
            }
        }
        mClazzName = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                     String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        if(!mIsLKModel){
            return methodVisitor;
        }
        methodVisitor = new AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {

            @Override
            public void visitCode() {
                super.visitCode();
                ILKDBPlguinApply apply = LKDBHelperVar.mLKDBs.get(name);
                if(apply != null){
                    apply.apply(mv);
                }
            }

        };

        return methodVisitor;

    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
