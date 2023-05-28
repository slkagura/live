package xyz.slkagura.ui.view.main

import android.content.Intent
import androidx.fragment.app.Fragment
import xyz.slkagura.R
import xyz.slkagura.common.base.BaseBindingActivity
import xyz.slkagura.common.base.BaseViewModel
import xyz.slkagura.common.utils.ViewModelUtil
import xyz.slkagura.databinding.ActivityMainBinding
import xyz.slkagura.grafika.CameraCaptureActivity
import xyz.slkagura.ui.view.camera.CameraActivity
import xyz.slkagura.ui.view.click.ClickActivity
import xyz.slkagura.ui.view.codec.CodecActivity
import xyz.slkagura.ui.view.grafika.GrafikaActivity
import xyz.slkagura.ui.view.live.LiveFragment
import xyz.slkagura.ui.view.manager.PackageManagerActivity
import xyz.slkagura.ui.view.opengl.OpenGLActivity
import xyz.slkagura.ui.view.permission.PermissionActivity
import xyz.slkagura.ui.view.sensor.SensorActivity

class MainActivity : BaseBindingActivity<ActivityMainBinding?>() {
    private var mViewModel: MainViewModel? = null
    override fun initLayoutId(): Int {
        return R.layout.activity_main
    }
    
    override fun initDataBinding(list: MutableList<BaseViewModel?>) {
        mViewModel = ViewModelUtil.get(this, MainViewModel::class.java)
        list.add(mViewModel)
    }
    
    override fun initViewBinding() {
        mBinding!!.v = this
        mBinding!!.vm = mViewModel
    }
    
    override fun onResume() {
        super.onResume()
    }
    
    fun onCameraClick() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
    
    fun onCodecClick() {
        val intent = Intent(this, CodecActivity::class.java)
        startActivity(intent)
    }
    
    fun onPermissionClick() {
        val intent = Intent(this, PermissionActivity::class.java)
        startActivity(intent)
    }
    
    fun onMultiClick() {
        val intent = Intent(this, ClickActivity::class.java)
        startActivity(intent)
    }
    
    fun onSensorClick() {
        val intent = Intent(this, SensorActivity::class.java)
        startActivity(intent)
    }
    
    fun onOpenGLClick() {
        val intent = Intent(this, OpenGLActivity::class.java)
        startActivity(intent)
    }
    
    fun onGrafikaClick() {
        val intent = Intent(this, CameraCaptureActivity::class.java)
        startActivity(intent)
    }
    
    fun onGrafikaRebuildClick() {
        val intent = Intent(this, GrafikaActivity::class.java)
        startActivity(intent)
    }
    
    fun onPackageManagerClick() {
        val intent = Intent(this, PackageManagerActivity::class.java)
        startActivity(intent)
    }
    
    private fun initFragment() {
        val fragment: Fragment = LiveFragment.getInstance()
        supportFragmentManager.beginTransaction().add(R.id.activity_main_cl_container, fragment).addToBackStack("live").commit()
    }
    
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
