package com.happysnaker.entry;

/**
 * plugin
 * 卡片对应实体类
 *
 * @author : wpf
 * @date : 2023-08-01 09:37
 **/
public class CardEntry {

        private String id;
        private String name;
        private String desc;
        private int atk;
        private int def;
        private int type;
        private int race;
        private int level;
        private int attribute;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }

        public void setAtk(int atk) {
            this.atk = atk;
        }
        public int getAtk() {
            return atk;
        }

        public void setDef(int def) {
            this.def = def;
        }
        public int getDef() {
            return def;
        }

        public void setType(int type) {
            this.type = type;
        }
        public int getType() {
            return type;
        }

        public void setRace(int race) {
            this.race = race;
        }
        public int getRace() {
            return race;
        }

        public void setLevel(int level) {
            this.level = level;
        }
        public int getLevel() {
            return level;
        }

        public void setAttribute(int attribute) {
            this.attribute = attribute;
        }
        public int getAttribute() {
            return attribute;
        }
}
