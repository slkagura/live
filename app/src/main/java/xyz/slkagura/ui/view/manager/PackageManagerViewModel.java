package xyz.slkagura.ui.view.manager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xyz.slkagura.common.base.BaseViewModel;
import xyz.slkagura.common.extension.log.Log;
import xyz.slkagura.common.utils.ContextUtil;
import xyz.slkagura.common.utils.FileUtil;
import xyz.slkagura.common.utils.PathUtil;

/**
 * @author slkagura
 * @version 1.0
 * @since 2023/5/14 17:15
 */
public class PackageManagerViewModel extends BaseViewModel {
    private static final String TAG = PackageManagerViewModel.class.getSimpleName();
    
    private final PackageManager mPackageManager = ContextUtil.getApplicationContext().getPackageManager();
    
    private final MutableLiveData<List<PackageInfo>> mPackages = new MutableLiveData<>();
    
    public void refresh() {
        ArrayList<PackageInfo> packages = (ArrayList<PackageInfo>) mPackageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        packages.sort((first, second) -> {
            String firstName = first.packageName;
            String secondName = second.packageName;
            if (firstName.equals(secondName)) {
                return 0;
            }
            int firstNameLength = firstName.length();
            int secondNameLength = secondName.length();
            int min = Math.min(firstNameLength, secondNameLength);
            char firstChar;
            char secondChar;
            for (int i = 0; i < min; i++) {
                firstChar = firstName.charAt(i);
                secondChar = secondName.charAt(i);
                if (firstChar == secondChar) {
                    continue;
                }
                return firstChar - secondChar;
            }
            return firstNameLength - secondNameLength;
        });
        mPackages.setValue(packages);
    }
    
    public void save() {
        List<PackageInfo> packages = mPackages.getValue();
        if (packages == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (PackageInfo info : packages) {
            sb.append("@REM ").append(loadLabel(info)).append(System.lineSeparator());
            sb.append("adb shell pm uninstall --user 0 ").append(info.packageName).append(System.lineSeparator());
        }
        File file = new File(PathUtil.getExternalDownloadsPath("slkagura"), "packages.txt");
        if (FileUtil.prepare(file, true)) {
            try (
                FileWriter fw = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fw)
            ) {
                writer.write(sb.toString());
                writer.flush();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
    
    public CharSequence loadLabel(PackageInfo info) {
        return info.applicationInfo.loadLabel(mPackageManager);
    }
    
    public LiveData<List<PackageInfo>> getPackages() {
        return mPackages;
    }
}
