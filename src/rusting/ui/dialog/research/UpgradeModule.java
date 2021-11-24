package rusting.ui.dialog.research;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import rusting.world.modules.ResearchModule;

/*
 * This really isn't a Dialog nor a Module.
 * My naming just sucks.
 * @author Pietro303HD
 */
public class UpgradeModule{
    public Seq<UpgradeNode> nodes = Seq.with();

    public void add(UpgradeNode node){
        nodes.add(node);
    }

    public static class UpgradeNode{
        public UpgradeNode parent = null;
        public ResearchModule value = null;

        public UpgradeNode(ResearchModule value){
            this.value = value;
        }

        public UpgradeNode(ResearchModule value, UpgradeNode parent){
            this(value);
            this.parent = parent;
        }
    }
}
