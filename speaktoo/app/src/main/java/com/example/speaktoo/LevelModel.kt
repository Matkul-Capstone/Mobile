package com.example.speaktoo

class LevelModel(var level_name: String, var imgid: Int) {
    fun getLevelName(): String {
        return level_name
    }

    fun setLevelName(level_name: String) {
        this.level_name = level_name
    }

    fun getImgId(): Int {
        return imgid
    }

    fun setImgId(imgid: Int) {
        this.imgid = imgid
    }
}