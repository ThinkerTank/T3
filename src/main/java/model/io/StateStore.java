package model.io;
import java.io.*;
import java.nio.file.*;
import model.service.Simulation;
public final class StateStore {
    private StateStore(){}
    public static void save(Simulation simulation,Path path)throws IOException{Files.createDirectories(path.toAbsolutePath().getParent());Path tmp=path.resolveSibling(path.getFileName()+".tmp");try(ObjectOutputStream out=new ObjectOutputStream(Files.newOutputStream(tmp))){synchronized(simulation){out.writeObject(simulation);}}try{Files.move(tmp,path,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException e){Files.move(tmp,path,StandardCopyOption.REPLACE_EXISTING);}}
    public static Simulation load(Path path)throws IOException{try(ObjectInputStream in=new ObjectInputStream(Files.newInputStream(path))){in.setObjectInputFilter(info->{Class<?> clazz=info.serialClass();if(info.depth()>2000||info.references()>100000||info.arrayLength()>100000)return ObjectInputFilter.Status.REJECTED;if(clazz==null)return ObjectInputFilter.Status.UNDECIDED;String n=clazz.getName();if(clazz.isArray()||clazz.isPrimitive()||n.startsWith("model.")||n.startsWith("java.lang.")||n.startsWith("java.time.")||n.startsWith("java.util.")||n.startsWith("java.io."))return ObjectInputFilter.Status.ALLOWED;return ObjectInputFilter.Status.REJECTED;});Object state=in.readObject();if(!(state instanceof Simulation s))throw new IOException("Save file has incorrect type");return s;}catch(ClassNotFoundException e){throw new IOException("Unknown saved state class",e);}}
}
