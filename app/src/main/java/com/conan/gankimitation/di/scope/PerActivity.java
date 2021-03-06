package com.conan.gankimitation.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Description：Dagger Activity单例Scope
 * Created by：JasmineBen
 * Time：2017/11/6
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {
}
