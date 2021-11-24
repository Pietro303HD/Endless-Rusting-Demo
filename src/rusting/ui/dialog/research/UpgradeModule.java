package rusting.ui.dialog.research;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;

public class UpgradeModule{
    public Seq<UpgradeNode> nodes = Seq.with();

    public UpgradeModule(){
        super();
    }

    public void add(UpgradeNode node){
        nodes.add(node);
    }

    public static class UpgradeNode{
        public UpgradeNode parent = null;
        public Content value = null;

        public UpgradeNode(Content value){
            this.value = value;
        };
    }
}
