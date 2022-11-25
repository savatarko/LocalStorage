import com.sun.source.tree.NewArrayTree;
import exceptions.FileNotFoundException;
import exceptions.StorageCountLimitException;
import org.apache.commons.io.FileUtils;
import spec.Configuration;
import spec.MyFile;
import spec.StorageManager;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalDrive extends StorageManager
{

    public static void main(String[] args) {
        LocalDrive ld=new LocalDrive();
        Configuration config=new Configuration(15000,"txt");
        config.getPathlimit().put("leale",5);
        config.getPathlimit().put("leale/leale2",7);
        ld.CreateStorage(config,"C:/Users/user/Desktop/asdefghh/");
        //    System.out.println(ld.storageLocation);
        ld.CreateDirectory("","leale");
        ld.CreateDirectory("leale/","leale2");
        ld.CreateDirectory("","leale3",10);




    }

    @Override
    public void CreateStorage(Configuration configuration, String path) {


        this.storageLocation=path;
        this.currentconfig=configuration;
        this.maxsize= currentconfig.getMaxsize();

        try{
            if(!new File(path).exists()) {
                File f = new File(path);
                f.mkdir();
                java.io.File file = new java.io.File(path + "/config.txt");
                FileWriter fw = new FileWriter(file);
                fw.write(configuration.toString());
                fw.close();
            } else {
                System.out.println("This storage already exists");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void LoadStorage(String path) {

    }

    @Override

    public void CreateDirectory(String path, String name) {
        try {

            File f=new File(storageLocation + path);
            if(!f.exists()){
                throw new FileNotFoundException(storageLocation + path);
            } else if(currentconfig.getPathlimit().containsKey(storageLocation + path) &&
                    currentconfig.getPathlimit().get(storageLocation + path)<new File(storageLocation + path).listFiles().length ){
                throw new StorageCountLimitException(storageLocation + path);
            }

            f=new File(storageLocation + path + name);
            f.mkdir();

        } catch(Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void CreateDirectory(String path, String name, int filelimit) {
        try {

            File f=new File(storageLocation + path);
            if(!f.exists()){
                throw new FileNotFoundException(storageLocation + path);
            } else if(currentconfig.getPathlimit().containsKey(storageLocation + path) &&
                    currentconfig.getPathlimit().get(storageLocation + path)<new File(storageLocation + path).listFiles().length ){
                throw new StorageCountLimitException(storageLocation + path);
            }

            f=new File(storageLocation + path + name);
            f.mkdir();
            currentconfig.getPathlimit().put(path,filelimit);
            File config=new File(storageLocation + configname);
            FileWriter fw=new FileWriter(config,true);
            fw.write("\n" + path + name + "|" + filelimit);
            fw.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    //uradi i dodaj config
    public void StoreFile(String path, MyFile file) {
        try {
            File f=new File(storageLocation + path + file.getFile().getName());

            if(
                    (!this.currentconfig.getForbidden().contains(getExtension(file.getFile()))) &&
                            ((!currentconfig.getPathlimit().containsKey(storageLocation + path) ||
                                    currentconfig.getPathlimit().get(storageLocation + path)<new File(storageLocation + path).listFiles().length)) &&
                            (FileUtils.sizeOfDirectory(new File(this.storageLocation))+file.getFile().length())<this.currentconfig.getMaxsize()
            )
            {
                Files.copy(file.getFile().toPath(), f.toPath());
            }
            else{
                System.out.println("ne moze");
            }


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override

    public void DeleteFromStorage(String path) {


        try{
            File f=new File(storageLocation + path);

            if(!f.exists()){
                throw new FileNotFoundException(storageLocation + path);
            }

            f.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override

    public void MoveFile(String oldPath, String newPath) {
        try{
            File f=new File(storageLocation + oldPath);
            if(!currentconfig.getPathlimit().containsKey(storageLocation + newPath) ||
                    currentconfig.getPathlimit().get(storageLocation + newPath)<new File(storageLocation + newPath).listFiles().length){
                throw new StorageCountLimitException(storageLocation + newPath);
            }
            f.renameTo(new File(storageLocation + newPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override

    public void DownloadFile(String sourcePath, String targetPath) {
        try{
            File source=new File(storageLocation + sourcePath);
            File target=new File(targetPath + source.getName());
            Files.copy(source.toPath(),target.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Rename(String path, String newName) {

        File f=new File(storageLocation + path);
        f.renameTo(new File(f.getParent() + "/" + newName));


    }

    @Override
    public List<MyFile> GetFiles(String path) {
        List<MyFile> out=new ArrayList<>();
        try {
            File f=new File(storageLocation + path);
            List<File> files= List.of(f.listFiles());

            for(File i: files){
                MyFile myf=new MyFile(i);
                out.add(myf);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


        return out;
    }

    @Override
    public List<MyFile> GetAllFiles(String path) {
        List<MyFile> out=new ArrayList<>();
        try {
            File f=new File(storageLocation + path);
            List<File> files= List.of(f.listFiles());

            for(File i: files){
                if(i.isDirectory()){
                    List<File> subfiles= List.of(i.listFiles());

                    for(File j:subfiles){
                        MyFile myf=new MyFile(j);
                        out.add(myf);
                    }
                }


            }



        } catch (Exception e) {
            e.printStackTrace();
        }


        return out;
    }

    @Override
    public List<MyFile> GetAllSubFiles(String path) {
        List<MyFile> out=new ArrayList<>();
        try {
            File f=new File(storageLocation + path);
            List<File> files= List.of(f.listFiles());

            for(File i: files){
                if(i.isDirectory()){
                    List<File> subfiles= List.of(i.listFiles());

                    for(File j:subfiles){
                        MyFile myf=new MyFile(j);
                        out.add(myf);
                    }
                }

                else{
                    MyFile myf=new MyFile(i);
                    out.add(myf);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


        return out;
    }

    @Override
    public List<MyFile> GetFilesType(String extension) {

        List<MyFile> out=new ArrayList<>();
        List<String> ext=new ArrayList<>();
        ext.addAll(Arrays.stream(extension.split(",")).toList());
        try {
            File f=new File(storageLocation);
            List<File> files=new ArrayList<>();
            files=ListAllFiles(files,f);

            for(File i: files){

                if(!i.isDirectory()) {

                    if(ext.contains(getExtension(i))){
                        MyFile myf=new MyFile(i);
                        out.add(myf);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    @Override
    public List<MyFile> GetFilesNamed(String name) {
        List<MyFile> out=new ArrayList<>();
        try {
            File f=new File(storageLocation);
            List<File> files=new ArrayList<>();
            files=ListAllFiles(files,f);

            for(File i: files){
                MyFile myf=new MyFile(i);

                if(myf.toString().contains(name))
                    out.add(myf);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    @Override
    public boolean IsContained(String path, List<String> filenames) {

        int rez=0,j=filenames.size();

        try {
            File f=new File(storageLocation + path);
            List<File> files= List.of(f.listFiles());

            for(File i: files){
                if(filenames.contains(i.getName()))
                    rez++;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rez==j;
    }

    @Override
    public String Locate(String name) {
        String out=null;
        List<File> files=new ArrayList<>();
        files=ListAllFiles(files,new File(storageLocation));

        for(File f:files){
            if(f.getName().equals(name))
                out=f.getPath();

        }

        return out;
    }



    public List<File> ListAllFiles(List<File> files,File root){
        List<File> subFiles=List.of(root.listFiles());

        for(File f: subFiles){
            if(f.isDirectory()){
                files=ListAllFiles(files,f);
            }
            else {
                files.add(f);
            }


        }

        return files;
    }

    @Override
    //uradi
    public List<MyFile> GetFilesTimeCreated(String path, String begintime, String endtime) {
        List<MyFile> out=new ArrayList<>();
        DateTimeFormatter format=DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'");
        LocalDateTime begin=LocalDateTime.parse(begintime,format);
        LocalDateTime end=LocalDateTime.parse(endtime,format);

        try {
            File f=new File(storageLocation);
            List<File> files= List.of(f.listFiles());

            for(File i: files){
                MyFile myf=new MyFile(i);
                LocalDateTime fileTime=LocalDateTime.parse(myf.getModtime(),format);
                if(fileTime.isAfter(begin) && fileTime.isBefore(end))
                    out.add(myf);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    @Override
    //uradi
    public List<MyFile> GetFilesTimeModified(String path, String begintime, String endtime) {
        List<MyFile> out=new ArrayList<>();
        DateTimeFormatter format=DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'");
        LocalDateTime begin=LocalDateTime.parse(begintime,format);
        LocalDateTime end=LocalDateTime.parse(endtime,format);

        try {
            File f=new File(storageLocation);
            List<File> files= List.of(f.listFiles());

            for(File i: files){
                MyFile myf=new MyFile(i);
                LocalDateTime fileTime=LocalDateTime.parse(myf.getModtime(),format);
                if(fileTime.isAfter(begin) && fileTime.isBefore(end))
                    out.add(myf);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }


    private static String getExtension(File f){
        if(f.exists() && !f.isDirectory()) {
            String[] s = f.getName().split("\\.");

            return s[1];
        }

        return null;
    }





}
